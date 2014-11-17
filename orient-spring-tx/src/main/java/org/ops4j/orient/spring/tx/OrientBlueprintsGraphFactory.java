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

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

/**
 * Factory for OrientDB graphs with Blueprints API.
 * 
 * @author Harald Wellmann
 * 
 */
public class OrientBlueprintsGraphFactory extends AbstractOrientDatabaseFactory {
    private OrientGraphFactory orientGraphFactory;
    private OrientGraph orientGraph;

    @Override
    public void init() {
        createPool();
        openDatabase();
    }
    
    @Override
    protected void createPool() {
        orientGraphFactory = new OrientGraphFactory(getUrl(), getUsername(), getPassword());
        orientGraphFactory.getDatabase(true, true);
        orientGraphFactory.setupPool(getMinPoolSize(), getMaxPoolSize());
    }

    @Override
    public ODatabase<?> openDatabase() {
        orientGraph = orientGraphFactory.getTx();
        return orientGraph.getRawGraph();
    }

    public OrientGraph graph() {
        return orientGraph;
    }

    public ODatabaseDocumentTx db() {
        return graph().getRawGraph();
    }

    @Override
    protected ODatabaseDocumentTx newDatabase() {
        return null;
    }

}
