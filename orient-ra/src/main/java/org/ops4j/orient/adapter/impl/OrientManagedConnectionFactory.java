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

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.TransactionSupport;
import javax.security.auth.Subject;

import org.ops4j.orient.adapter.api.OrientDatabaseConnectionFactory;
import org.ops4j.orient.adapter.api.OrientDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 * 
 */
// @formatter:off
@ConnectionDefinition(
    connectionFactory = OrientDatabaseConnectionFactory.class, 
    connectionFactoryImpl = OrientDatabaseConnectionFactoryImpl.class, 
    connection = OrientDatabaseConnection.class,
    connectionImpl = OrientDatabaseConnectionImpl.class)
// @formatter:on
public class OrientManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation, TransactionSupport {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(OrientManagedConnectionFactory.class);
    
    
    private PrintWriter logWriter;
    private OrientResourceAdapter ra;

    @ConfigProperty(defaultValue = "document")
    private String type;
    
    @ConfigProperty
    private String connectionUrl;
    
    @ConfigProperty(defaultValue = "admin")
    private String username;
    
    @ConfigProperty(defaultValue = "admin")
    private String password;
    

    public OrientManagedConnectionFactory() {
        this.logWriter = new PrintWriter(System.out);
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        log.debug("creating managed connection factory");
        validate();
        return new OrientDatabaseConnectionFactoryImpl(this, cxManager);
    }
    
    private void validate() throws ResourceException {
        if (connectionUrl == null || connectionUrl.trim().isEmpty()) {
            throw new ResourceException("configuration property [connectionUrl] must not be empty");
        }
        
        if (!Arrays.asList("document", "graph", "object").contains(type)) {
            throw new ResourceException("configuration property [type] must be one of 'document', 'graph', 'object'");            
        }
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        throw new ResourceException("unmanaged environments are not supported");
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject,
        ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        log.debug("creating managed connection");
        return new OrientManagedConnection(this, cxRequestInfo);
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "resource" })
    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
        ConnectionRequestInfo cxRequestInfo) throws ResourceException {

        Set<ManagedConnection> connections = connectionSet;

        for (ManagedConnection connection : connections) {
            if (connection instanceof OrientManagedConnection) {
                OrientManagedConnection orientConnection = (OrientManagedConnection) connection;
                ConnectionRequestInfo cri = orientConnection.getConnectionRequestInfo();
                if (cri == null || cri.equals(cxRequestInfo)) {
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

    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
        OrientManagedConnectionFactory other = (OrientManagedConnectionFactory) obj;
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
