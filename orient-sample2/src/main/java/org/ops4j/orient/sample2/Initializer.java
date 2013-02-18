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

package org.ops4j.orient.sample2;


import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.ops4j.orient.sample2.model.Author;
import org.ops4j.orient.sample2.model.Book;

import com.orientechnologies.orient.core.entity.OEntityManager;


/**
 * @author Harald Wellmann
 *
 */
@Stateless
public class Initializer {
    
    @Inject
    private ObjectDatabase odb;
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void registerEntityClasses() {
        OEntityManager em = odb.db().getEntityManager();
        em.registerEntityClass(Author.class);
        em.registerEntityClass(Book.class);
    }
    
    public void createEntities() {
        Book hobbit = odb.db().newInstance(Book.class);
        hobbit.setTitle("The Hobbit");
        odb.db().save(hobbit);
    }

}
