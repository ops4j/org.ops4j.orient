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
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Factory for OrientDB graphs with Blueprints API.
 * 
 * @author Harald Wellmann
 * 
 */
public class OrientBlueprintsGraphFactory extends AbstractOrientDatabaseFactory {

    private ODatabaseDocumentTx db;
    private ODatabasePoolBase<ODatabaseDocumentTx> pool;

    @Override
    protected void createPool() {
        pool = new ODatabaseDocumentPool(getUrl(), getUsername(), getPassword());
        pool.setup(getMinPoolSize(), getMaxPoolSize());
    }

    @Override
    public ODatabaseDocumentTx openDatabase() {
        db = pool.acquire();
        return db;
    }

    public ODatabaseDocumentTx db() {
        return (ODatabaseDocumentTx) super.db();
    }

    public OrientGraph graph() {
        return new OrientGraph((ODatabaseDocumentTx) super.db(), false);
    }

    protected ODatabaseDocumentTx newDatabase() {
        return new ODatabaseDocumentTx(getUrl());
    }

}
