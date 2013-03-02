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

/**
 * @author Harald Wellmann
 * 
 */
public class TransactionalObjectService {
    
    private static Logger log = LoggerFactory.getLogger(TransactionalObjectService.class);

    @Autowired
    private OrientObjectDatabaseManager dbm;


    @Transactional
    public void commitAutomatically() {
        log.debug("commitAutomatically db.hashCode() = {}", dbm.db().hashCode());
        assertThat(dbm.db().getTransaction().isActive(), is(true));

        Person person = dbm.db().newInstance(Person.class);
        person.setFirstName("Donald");
        person.setLastName("Duck");
        dbm.db().save(person);
    }

    @Transactional
    public void rollbackOnError() {
        assertThat(dbm.db().getTransaction().isActive(), is(true));

        Person person = dbm.db().newInstance(Person.class);
        person.setFirstName("Donald");
        person.setLastName("Duck");
        dbm.db().save(person);

        throw new RuntimeException();
    }
    
    @Transactional
    public long count() {
        return dbm.db().countClass(Person.class);
    }
}
