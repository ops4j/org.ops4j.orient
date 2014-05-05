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

package org.ops4j.orient.spring.tx.graph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.orient.spring.tx.OrientBlueprintsGraphFactory;
import org.ops4j.orient.spring.tx.OrientTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link OrientTransactionManager} with a {@link OrientBlueprintsGraphFactory}.
 * @author Harald Wellmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GraphSpringTestConfig.class)
public class GraphDatabaseTransactionTest {

    @Autowired
    private TransactionalGraphService service;

    @Autowired
    private OrientBlueprintsGraphFactory dbf;

    private ODatabaseDocumentTx db;

    @Before
    public void setUp() {
        db = dbf.openDatabase();
        OSchema schema = db.getMetadata().getSchema();
        if (!schema.existsClass("TestVertex")) {
			dbf.graph().createVertexType("TestVertex");
        }
        ORecordIteratorClass<ODocument> it = db.browseClass("TestVertex");
        while (it.hasNext()) {
            it.next().delete();
        }
    }

    @Test
    public void shouldCommit() {
        service.commitAutomatically();
        assertTrue(!db.getTransaction().isActive());

		assertTrue(service.count() == 1);
    }

    @Test
    public void rollbackWithAnnotationTest() {
        assertTrue(!db.getTransaction().isActive());
        try {
            service.rollbackOnError();
        }
        catch (Exception e) {

        }
        assertTrue(!db.getTransaction().isActive());
        assertTrue(service.count() == 0);
    }
    
    @Test
    public void commitMultiThreaded() throws InterruptedException {
        
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorService.submit(new CommitTask());
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);
        
    }
    
    class CommitTask implements Runnable {

        @Override
        public void run() {
            service.commitAutomatically();            
        }
        
    }

}
