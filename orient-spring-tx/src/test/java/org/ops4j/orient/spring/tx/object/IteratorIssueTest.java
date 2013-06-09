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

import javax.persistence.Id;
import javax.persistence.Version;

import org.junit.Test;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;

/**
 * Iterators seem to be broken in OrientDB 1.3.0. In this test, the iterator internally goes into
 * an infinite loop.
 * <p>
 * The issue is fixed in OrientDB 1.4.0.
 * 
 * @author Harald Wellmann
 *
 */
public class IteratorIssueTest {
    
    public static class Person {
        
        @Id
        private Object id;
        
        @Version
        private Integer version;
        
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void iteratorShouldTerminate() {
        OObjectDatabaseTx db = new OObjectDatabaseTx("memory:test");
        db.create();
        db.getEntityManager().registerEntityClass(Person.class);

        db.begin();
        Person person = new Person();
        person = db.save(person);
        db.commit();
        
        db.begin();
        db.delete(person);
        db.commit();    
        
        db.begin();
        Person person2 = new Person();
        person2 = db.save(person2);
        OObjectIteratorClass<Person> it = db.browseClass(Person.class);
        int numPersons = 0;
        while (it.hasNext()) {
            numPersons++;
            it.next();
        }
        db.commit();
        assertThat(numPersons, is(1));
    }
}
