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

package org.ops4j.orient.adapter.impl;

import org.ops4j.orient.adapter.api.OrientDatabaseConnection;
import org.ops4j.orient.adapter.api.OrientDatabaseConnectionInvalidException;

import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Harald Wellmann
 * 
 */
public class OrientDatabaseConnectionImpl implements OrientDatabaseConnection {

    private OrientManagedConnectionImpl mc;
    private ODatabaseInternal<?>        db;
    private boolean valid = true;

    public OrientDatabaseConnectionImpl(ODatabaseInternal<?> db, OrientManagedConnectionImpl mc) {
        this.db = db;
        this.mc = mc;
    }

    @Override
    public ODatabaseDocumentTx document() {
        checkValidity();
        return (ODatabaseDocumentTx) db;
    }

    @Override
    public OObjectDatabaseTx object() {
        checkValidity();
        return (OObjectDatabaseTx) db;
    }

    @Override
    public OrientGraph ograph() {
        checkValidity();
        return new OrientGraph((ODatabaseDocumentTx) db);
    }

    @Override
    public void close() {
        mc.close();
    }

    protected synchronized void setValid(boolean valid) {
        this.valid = valid;
    }

    protected synchronized boolean isValid() {
        return valid;
    }

    private void checkValidity() {
        if (!isValid()) {
            throw new OrientDatabaseConnectionInvalidException();
        }
    }
}
