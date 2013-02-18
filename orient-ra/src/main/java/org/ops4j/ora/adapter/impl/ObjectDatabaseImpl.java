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

package org.ops4j.ora.adapter.impl;

import org.ops4j.ora.adapter.api.ObjectDatabase;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;


/**
 * @author Harald Wellmann
 *
 */
public class ObjectDatabaseImpl extends OObjectDatabaseTx implements ObjectDatabase {

    private OrientManagedConnectionImpl mc;

    public ObjectDatabaseImpl(String iURL, OrientManagedConnectionImpl mc) {
        super(iURL);
        this.mc = mc;
    }
    
    @Override
    public Object detach(Object iPojo) {
        return super.detach(iPojo);
    }
    
    @Override
    public void close() {
        super.close();        
        mc.close();
    }
    
    void closeInternal() {
        super.close();
    }
}
