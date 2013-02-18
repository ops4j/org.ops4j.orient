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

import java.util.HashSet;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.ops4j.ora.adapter.api.OrientManagedConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 * 
 */
// @formatter:off
@Connector(
    reauthenticationSupport = false, 
    transactionSupport = TransactionSupport.TransactionSupportLevel.LocalTransaction, 
    version = "0.1.0", 
    vendorName = "OPS4J",
    eisType = "OrientDB")
// @formatter:on
public class OrientResourceAdapter implements ResourceAdapter {
    
    private static Logger log = LoggerFactory.getLogger(OrientResourceAdapter.class);

    private Set<OrientManagedConnectionFactory> factories = new HashSet<OrientManagedConnectionFactory>();    
    
    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        log.debug("starting OrientResourceAdapter");
        for (OrientManagedConnectionFactory factory : factories) {
        }
    }

    @Override
    public void stop() {
        log.debug("stopping OrientResourceAdapter");
        for (OrientManagedConnectionFactory factory : factories) {
        }
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec)
        throws ResourceException {
        // not used
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        // not used
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return null;
    }

    public void addFactory(OrientManagedConnectionFactory factory) {
        factories.add(factory);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
