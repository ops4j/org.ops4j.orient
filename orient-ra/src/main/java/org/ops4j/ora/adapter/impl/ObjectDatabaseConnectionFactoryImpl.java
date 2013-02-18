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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.ops4j.ora.adapter.api.ObjectDatabase;
import org.ops4j.ora.adapter.api.ObjectDatabaseConnectionFactory;
import org.ops4j.ora.adapter.api.OrientManagedConnectionFactory;


/**
 * @author Harald Wellmann
 *
 */
public class ObjectDatabaseConnectionFactoryImpl implements ObjectDatabaseConnectionFactory {

    private static final long serialVersionUID = 1L;

    private OrientManagedConnectionFactory mcf;
    private ConnectionManager cm;
    private Reference reference;
    
    /**
     * 
     */
    public ObjectDatabaseConnectionFactoryImpl(OrientManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }
    
    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Reference getReference() throws NamingException {
        return reference;
    }

    @Override
    public ObjectDatabase createConnection() throws ResourceException {
        return (ObjectDatabase) cm.allocateConnection(mcf, null);
    }

}
