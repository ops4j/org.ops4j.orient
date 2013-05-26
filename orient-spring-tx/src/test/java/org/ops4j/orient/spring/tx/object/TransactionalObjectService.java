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
import static org.junit.Assert.assertThat;

import java.util.List;

import org.ops4j.orient.spring.tx.OrientObjectDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;

/**
 * @author Harald Wellmann
 * 
 */
@Transactional
public class TransactionalObjectService {
    
    @Autowired
    private OrientObjectDatabaseFactory dbf;

    @Transactional(propagation = Propagation.NEVER)
    public void registerEntityClasses() {
        registerClass(Person.class);
    }
    
    private <T> void registerClass(Class<T> klass) {
        OObjectDatabaseTx db = dbf.openDatabase();
        OEntityManager em = db.getEntityManager();
        OSchema schema = db.getMetadata().getSchema();
        if (schema.getClass(klass) == null) {
            schema.createClass(klass);
        }
        em.registerEntityClass(klass);
        db.close();
    }
    

    public void clear() {
        OObjectDatabaseTx db = dbf.db();
        
        OSQLSynchQuery<Person> q = new OSQLSynchQuery<Person>("select from Person");
        List<Person> persons = db.query(q);
        for (Person person : persons) {
            db.delete(person);
        }
    }

    public void commitAutomatically() {
        assertThat(dbf.db().getTransaction().isActive(), is(true));

        Person person = dbf.db().newInstance(Person.class);
        person.setFirstName("Donald");
        person.setLastName("Duck");
        person = dbf.db().save(person);
    }

    public void rollbackOnError() {
        assertThat(dbf.db().getTransaction().isActive(), is(true));

        Person person = dbf.db().newInstance(Person.class);
        person.setFirstName("Donald");
        person.setLastName("Duck");
        dbf.db().save(person);

        throw new RuntimeException();
    }
    
    public long countByQuery() {
        OObjectDatabaseTx db = dbf.db();
        
        OSQLSynchQuery<ODocument> q = new OSQLSynchQuery<ODocument>("select count(*) from Person");
        List<ODocument> results = db.query(q);
        assert results.size() == 1;
        return results.get(0).field("count");
    }

    public long countByClass() {
        OObjectDatabaseTx db = dbf.db();
        return db.countClass(Person.class);
    }

    public long countByIterator() {
        OObjectDatabaseTx db = dbf.db();
        OObjectIteratorClass<Person> it = db.browseClass(Person.class);
        long numObjects = 0;
        while (it.hasNext()) {
            it.next();
            numObjects++;
        }
        return numObjects;
    }
}
