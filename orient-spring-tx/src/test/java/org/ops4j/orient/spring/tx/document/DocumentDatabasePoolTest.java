/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.orient.spring.tx.document;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * This test verifies some properties of pooled database we rely on for suspending and resuming
 * transactions.
 * 
 * @author Harald Wellmann
 * @author André Frimberger
 * 
 */
public class DocumentDatabasePoolTest {

    private static final String URL = "local:target/docPoolTest";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * We cannot open a database from a pool unless the underlying database exists.
     */
    @Before
    public void setUp() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(URL);
        if (!db.exists()) {
            db.create();
        }
        db.close();
    }

    @Test
    public void pooledInstancesAreNotSame() throws ExecutionException, InterruptedException {
        ODatabaseDocumentPool pool = new ODatabaseDocumentPool(URL, USER, PASSWORD);
        pool.setup(1, 5);

        // Since 1.7 connections are reused on a per thread basis
        Future<ODatabaseDocumentTx> future1 = executor.submit(new PooledConnectionThread(pool));
        Future<ODatabaseDocumentTx> future2 = executor.submit(new PooledConnectionThread(pool));

        // Get DB from pool
        ODatabaseDocumentTx db1 = future1.get();

        // Get another DB from pool
        ODatabaseDocumentTx db2 = future2.get();

        // This is a different instance which is now current.
        assertThat(db1, is(not(sameInstance(db2))));

        // Close first DB. Second DB remains current.
        ODatabaseRecordThreadLocal.INSTANCE.set(db1);
        db1.close();

        db2.close();
    }

    private class PooledConnectionThread implements Callable<ODatabaseDocumentTx> {

        private final ODatabaseDocumentPool pool;
        private final ODatabaseRecordThreadLocal record = ODatabaseRecordThreadLocal.INSTANCE;

        private PooledConnectionThread(ODatabaseDocumentPool pool) {
            this.pool = pool;
        }

        @Override
        public ODatabaseDocumentTx call() throws Exception {
            // Get DB from pool
            ODatabaseDocumentTx db = pool.acquire();

            assertThat(record.get(), is((ODatabaseRecord) db.getUnderlying()));
            assertThat((ODatabaseDocumentTx) record.get().getDatabaseOwner(), is(db));

            return db;
        }
    }
}
