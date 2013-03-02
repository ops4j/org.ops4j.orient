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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Harald Wellmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class OrientTransactionManagerTest {

    @Autowired
    private TransactionalService service;

    @Autowired
    private OrientDatabaseManager dbManager;

    private ODatabaseDocumentTx db;

    @Before
    public void setUp() {
        db = (ODatabaseDocumentTx) dbManager.getDatabase();
        OSchema schema = db.getMetadata().getSchema();
        if (!schema.existsClass("TestDoc")) {
            schema.createClass("TestDoc");
        }
        ORecordIteratorClass<ODocument> it = db.browseClass("TestDoc");
        while (it.hasNext()) {
            it.next().delete();
        }
    }

    @Test
    public void shouldCommit() {
        service.commitAutomatically("TestDoc");
        assertTrue(!db.getTransaction().isActive());

        assertTrue(db.countClass("TestDoc") == 1);
    }

    @Test
    public void rollbackWithAnnotationTest() {
        assertTrue(!db.getTransaction().isActive());
        try {
            service.rollbackOnError("TestDoc");
        }
        catch (Exception e) {

        }
        assertTrue(!db.getTransaction().isActive());
        assertTrue(db.countClass("TestDoc") == 0);
    }

}
