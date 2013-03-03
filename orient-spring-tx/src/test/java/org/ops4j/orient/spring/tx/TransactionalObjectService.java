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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;

/**
 * @author Harald Wellmann
 * 
 */
public class TransactionalObjectService {
    
    private static Logger log = LoggerFactory.getLogger(TransactionalObjectService.class);

    @Autowired
    private OrientObjectDatabaseFactory dbf;

    @Transactional(propagation = Propagation.NEVER)
    public void registerEntityClasses() {
        dbf.db().getEntityManager().registerEntityClass(Person.class);
    }

    @Transactional
    public void clear() {
        OObjectDatabaseTx db = dbf.db();
        OObjectIteratorClass<Person> it = db.browseClass(Person.class);
        while (it.hasNext()) {
            db.delete(it.next());
        }
    }

    @Transactional
    public void commitAutomatically() {
        log.debug("commitAutomatically db.hashCode() = {}", dbf.db().hashCode());
        assertThat(dbf.db().getTransaction().isActive(), is(true));

        Person person = dbf.db().newInstance(Person.class);
        person.setFirstName("Donald");
        person.setLastName("Duck");
        dbf.db().save(person);
    }

    @Transactional
    public void rollbackOnError() {
        assertThat(dbf.db().getTransaction().isActive(), is(true));

        Person person = dbf.db().newInstance(Person.class);
        person.setFirstName("Donald");
        person.setLastName("Duck");
        dbf.db().save(person);

        throw new RuntimeException();
    }
    
    @Transactional
    public long count() {
        return dbf.db().countClass(Person.class);
    }
}
