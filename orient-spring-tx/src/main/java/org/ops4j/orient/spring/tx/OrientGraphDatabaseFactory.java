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

import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;

/**
 * @author Harald Wellmann
 * 
 */
public class OrientGraphDatabaseFactory extends AbstractOrientDatabaseFactory {

    private OGraphDatabase db;
    private ODatabasePoolBase<OGraphDatabase> pool;

    @Override
    protected void createPool() {
        pool = new OGraphDatabasePool(getUrl(), getUsername(), getPassword());
        pool.setup(getMinPoolSize(), getMaxPoolSize());        
    }


    @Override
    public OGraphDatabase openDatabase() {
        OGraphDatabasePool pool = new OGraphDatabasePool(getUrl(), getUsername(), getPassword());
        pool.setup(getMinPoolSize(), getMaxPoolSize());
        db = pool.acquire();
        return db;
    }
    
    public OGraphDatabase db() {
        return (OGraphDatabase) super.db();
    }
    

    protected OGraphDatabase newDatabase() {
        return new OGraphDatabase(getUrl());
    }    
}
