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

import org.ops4j.orient.adapter.api.OrientDatabaseConnectionInvalidException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;


/**
 * @author Harald Wellmann
 * 
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class DatabaseInitializer {
    @Inject
    private LibraryService initializer;

    @PostConstruct
    public void init() throws OrientDatabaseConnectionInvalidException {
        initializer.registerEntityClasses();
        initializer.createEntities();
    }
}
