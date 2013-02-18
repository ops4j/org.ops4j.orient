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

import static javax.resource.spi.ConnectionEvent.CONNECTION_CLOSED;
import static javax.resource.spi.ConnectionEvent.LOCAL_TRANSACTION_COMMITTED;
import static javax.resource.spi.ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK;
import static javax.resource.spi.ConnectionEvent.LOCAL_TRANSACTION_STARTED;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.ops4j.ora.adapter.api.ObjectDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Harald Wellmann
 *
 */
public class OrientManagedConnectionImpl implements ManagedConnection, LocalTransaction, Closeable {
    
    private static Logger log = LoggerFactory.getLogger(OrientManagedConnectionImpl.class);

    private OrientManagedConnectionFactoryImpl mcf;
    private ObjectDatabaseImpl db;
    private PrintWriter logWriter;
    private List<ConnectionEventListener> listeners = new ArrayList<ConnectionEventListener>();
    private ConnectionRequestInfo cri;

    /**
     * @param orientManagedConnectionFactoryImpl
     * @param database
     */
    public OrientManagedConnectionImpl(OrientManagedConnectionFactoryImpl mcf, 
        ConnectionRequestInfo cri) {
        this.mcf = mcf;
        this.cri = cri;
        log.debug("instatiating ObjectDataseImpl for {}", mcf.getConnectionUrl());
        this.db = new ObjectDatabaseImpl(mcf.getConnectionUrl(), this);
        log.debug("opening database for user = {}", mcf.getUsername());
        db.open(mcf.getUsername(), mcf.getPassword());
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
        throws ResourceException {
        log.debug("getConnection()");
        return db;
    }

    @Override
    public void destroy() throws ResourceException {
        log.debug("destroy()");
        System.out.println("destroy()");
        db.close();
    }

    @Override
    public void cleanup() throws ResourceException {
        log.debug("cleanup()");
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        log.debug("associateConnection()");
        this.db = (ObjectDatabaseImpl) connection;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);            
        }
    }
    

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new ResourceException("OrientDB resource adapter does not support XA transactions");
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return this;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new OrientManagedConnectionMetaData();
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
    public void begin() throws ResourceException {
        log.debug("begin()");
        db.begin();
        fireConnectionEvent(LOCAL_TRANSACTION_STARTED);
    }

    @Override
    public void commit() throws ResourceException {
        log.debug("commit()");
        db.commit();
        fireConnectionEvent(LOCAL_TRANSACTION_COMMITTED);
    }

    @Override
    public void rollback() throws ResourceException {
        log.debug("rollback()");
        db.rollback();
        fireConnectionEvent(LOCAL_TRANSACTION_ROLLEDBACK);
    }

    public void fireConnectionEvent(int event) {
        ConnectionEvent connnectionEvent = new ConnectionEvent(this, event);
        connnectionEvent.setConnectionHandle(db);
        for (ConnectionEventListener listener : this.listeners) {
            switch (event) {
                case LOCAL_TRANSACTION_STARTED:
                    listener.localTransactionStarted(connnectionEvent);
                    break;
                case LOCAL_TRANSACTION_COMMITTED:
                    listener.localTransactionCommitted(connnectionEvent);
                    break;
                case LOCAL_TRANSACTION_ROLLEDBACK:
                    listener.localTransactionRolledback(connnectionEvent);
                    break;
                case CONNECTION_CLOSED:
                    listener.connectionClosed(connnectionEvent);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event: " + event);
            }
        }
    }

    @Override
    public void close() {
        log.debug("close()");
        if (!db.isClosed()) {
            db.closeInternal();
        }
        fireConnectionEvent(CONNECTION_CLOSED);
    }
    
    public ConnectionRequestInfo getConnectionRequestInfo() {
        return cri;
    }
}
