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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Harald Wellmann
 * 
 */
public class TransactionalGraphService {
    
    private static Logger log = LoggerFactory.getLogger(TransactionalGraphService.class);

    @Autowired
    private OrientGraphDatabaseFactory dbf;


    @Transactional
    public void commitAutomatically() {
        log.debug("commitAutomatically db.hashCode() = {}", dbf.db().hashCode());
        assertThat(dbf.db().getTransaction().isActive(), is(true));

        ODocument vertex = dbf.db().createVertex("TestVertex");
        vertex.field("test", "test");
        dbf.db().save(vertex);
    }

    @Transactional
    public void rollbackOnError() {
        assertThat(dbf.db().getTransaction().isActive(), is(true));

        ODocument vertex = dbf.db().createVertex("TestVertex");
        vertex.field("test", "test");
        dbf.db().save(vertex);

        throw new RuntimeException();
    }
    
    @Transactional
    public long count() {
        return dbf.db().countClass("TestVertex");
    }
}
