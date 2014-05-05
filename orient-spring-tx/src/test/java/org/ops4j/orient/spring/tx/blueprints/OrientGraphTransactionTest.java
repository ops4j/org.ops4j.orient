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

package org.ops4j.orient.spring.tx.blueprints;

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
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link OrientTransactionManager} with a {@link OrientBlueprintsGraphFactory}.
 * @author Harald Wellmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GraphSpringTestConfig.class)
public class OrientGraphTransactionTest {

    @Autowired
    private TransactionalGraphService service;

    @Autowired
    private OrientBlueprintsGraphFactory dbf;

    private ODatabaseDocumentTx db;
    
    private OrientGraph graph;

    @Before
    public void setUp() {
        db = dbf.openDatabase();
        graph = dbf.graph();

        for (Vertex v : graph.getVertices()) {
            graph.removeVertex(v);
        }
        graph.commit();
    }

    @Test
    public void shouldCommit() {
		assertThat(service.count(), is(0L));
        service.commitAutomatically();
		assertThat(db.getTransaction().isActive(), is(false));

        assertThat(service.count(), is(1L));
    }

    @Test
    public void rollbackWithAnnotationTest() {
        assertThat(service.count(), is(0L));
        assertTrue(!db.getTransaction().isActive());
        try {
            service.rollbackOnError();
        }
        catch (Exception e) {

        }
        assertThat(db.getTransaction().isActive(), is(false));
        assertThat(service.count(), is(0L));
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
