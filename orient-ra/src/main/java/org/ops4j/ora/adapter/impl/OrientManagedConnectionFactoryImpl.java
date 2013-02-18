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

import java.io.PrintWriter;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.security.auth.Subject;

import org.ops4j.ora.adapter.api.ObjectDatabase;
import org.ops4j.ora.adapter.api.ObjectDatabaseConnectionFactory;
import org.ops4j.ora.adapter.api.OrientManagedConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * @author Harald Wellmann
 * 
 */
// @formatter:off
@ConnectionDefinition(
    connectionFactory = ObjectDatabaseConnectionFactory.class, 
    connectionFactoryImpl = ObjectDatabaseConnectionFactoryImpl.class, 
    connection = ObjectDatabase.class,
    connectionImpl = ObjectDatabaseImpl.class)
// @formatter:on
public class OrientManagedConnectionFactoryImpl implements OrientManagedConnectionFactory {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(OrientManagedConnectionFactoryImpl.class);
    
    
    private PrintWriter logWriter;
    private OrientResourceAdapter ra;

    @ConfigProperty
    private String connectionUrl;
    
    @ConfigProperty(defaultValue = "admin")
    private String username;
    
    @ConfigProperty(defaultValue = "admin")
    private String password;
    

    public OrientManagedConnectionFactoryImpl() {
        this.logWriter = new PrintWriter(System.out);
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        log.debug("creating managed connection factory");
        return new ObjectDatabaseConnectionFactoryImpl(this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        throw new ResourceException("unmanaged environments are not supported");
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject,
        ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        log.debug("creating managed connection");
        return new OrientManagedConnectionImpl(this, cxRequestInfo);
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "resource" })
    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
        ConnectionRequestInfo cxRequestInfo) throws ResourceException {

        Set<ManagedConnection> connections = connectionSet;

        for (ManagedConnection connection : connections) {
            if (connection instanceof OrientManagedConnectionImpl) {
                OrientManagedConnectionImpl orientConnection = (OrientManagedConnectionImpl) connection;
                if (orientConnection.getConnectionRequestInfo().equals(cxRequestInfo)) {
                    return connection;
                }
            }
        }
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return ra;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        this.ra = (OrientResourceAdapter) ra;
        this.ra.addFactory(this);
    }

    @Override
    public TransactionSupportLevel getTransactionSupport() {
        return TransactionSupportLevel.LocalTransaction;
    }

    /**
     * @return the connectionUrl
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    
    /**
     * @param connectionUrl the connectionUrl to set
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }
    
    

    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ra == null) ? 0 : ra.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrientManagedConnectionFactoryImpl other = (OrientManagedConnectionFactoryImpl) obj;
        if (ra == null) {
            if (other.ra != null) {
                return false;
            }
        }
        else if (!ra.equals(other.ra)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
