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

package org.ops4j.orient.sample2;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.resource.ResourceException;

import org.ops4j.orient.adapter.api.OrientDatabaseConnection;
import org.ops4j.orient.adapter.api.OrientDatabaseConnectionFactory;
import org.ops4j.orient.adapter.api.OrientDatabaseConnectionInvalidException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;


/**
 * CDI producer for request scoped database connections.
 * <p>
 * This producer class is application scoped so that the injected OrientDatabaseConnectionFactory
 * resource will be created only once.
 * <p>
 * The connections have to be request scoped to ensure a new connection and transaction for
 * every request.
 * 
 * @author Harald Wellmann
 * 
 */
@ApplicationScoped
public class OrientDatabaseConnectionProducer {
    private static Logger log = LoggerFactory.getLogger(OrientDatabaseConnectionProducer.class);

    @Resource(lookup = "orient/library")
    private OrientDatabaseConnectionFactory cf;

    @Produces
    @RequestScoped
    public OrientDatabaseConnection openDatabase() {
        try {
            log.info("producing database connection");
            OrientDatabaseConnection db = cf.createConnection();
            return db;
        }
        catch (ResourceException exc) {
            throw new RuntimeException(exc);
        }
    }
    public void close(@Disposes OrientDatabaseConnection connection) {
        log.info("disposing database connection");
        connection.close();
    }
    
    public static OObjectDatabaseTx db(OrientDatabaseConnection connection) throws OrientDatabaseConnectionInvalidException {
        return connection.object();
    }
}
