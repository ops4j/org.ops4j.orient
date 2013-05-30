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

package org.ops4j.orient.spring.tx.object;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * This test verifies some properties of pooled database we rely on for suspending and
 * resuming transactions.
 * 
 * @author Harald Wellmann
 * 
 */
public class ObjectDatabasePoolTest {
    
    private static final String URL = "local:target/poolTest";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";
    
    private ODatabaseRecordThreadLocal record = ODatabaseRecordThreadLocal.INSTANCE;

    /**
     * We cannot open a database from a pool unless the underlying database exists.
     */
    @Before
    public void setUp() {
        OObjectDatabaseTx db = new OObjectDatabaseTx(URL);
        if (!db.exists()) {
            db.create();
        }
        db.close();
    }

    @Test
    public void pooledInstancesAreNotSame() {
        OObjectDatabasePool pool = new OObjectDatabasePool(URL, USER, PASSWORD);
        pool.setup();

        // Get DB from pool
        OObjectDatabaseTx db1 = pool.acquire();
        // This one is now current
        assertThat(record.get(), is((ODatabaseRecord) db1.getUnderlying()));
        assertThat((OObjectDatabaseTx) record.get().getDatabaseOwner(), is(db1));

        // Get another DB from pool
        OObjectDatabaseTx db2 = pool.acquire();
        
        // This is a different instance which is now current.
        assertThat(db1, is(not(sameInstance(db2))));
        assertThat((OObjectDatabaseTx) record.get().getDatabaseOwner(), is(db2));
        
        // Close first DB. Second DB remains current.
        db1.close();
        assertThat((OObjectDatabaseTx) record.get().getDatabaseOwner(), is(db2));
        
        db2.close();
    }
}
