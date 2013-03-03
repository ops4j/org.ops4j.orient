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

package org.ops4j.orient.spring.tx;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Harald Wellmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ObjectSpringTestConfig.class)
public class ObjectDatabaseTransactionTest {

    @Autowired
    private TransactionalObjectService service;

    @Autowired
    private OrientObjectDatabaseFactory dbf;

    @Before
    public void setUp() {
        service.registerEntityClasses();
        service.clear();
    }

    @Test
    public void shouldCommit() {
        service.commitAutomatically();
        assertTrue(!dbf.db().getTransaction().isActive());

        assertTrue(service.count() == 1);
    }

    @Test
    public void rollbackWithAnnotationTest() {
        assertTrue(!dbf.db().getTransaction().isActive());
        try {
            service.rollbackOnError();
        }
        catch (Exception e) {

        }
        assertTrue(!dbf.db().getTransaction().isActive());
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
