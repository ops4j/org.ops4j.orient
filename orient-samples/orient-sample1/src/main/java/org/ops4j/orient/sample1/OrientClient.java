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

package org.ops4j.orient.sample1;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.ops4j.orient.adapter.api.OrientDatabaseConnection;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;


/**
 * @author Harald Wellmann
 *
 */
@Singleton
@Startup
public class OrientClient {
    

    @Inject
    private OrientDatabaseConnection connection;
    
    @PostConstruct
    public void init() {
        OObjectDatabaseTx db = connection.object();
        System.out.println("DB exists: "+ db.exists());
    }
}
