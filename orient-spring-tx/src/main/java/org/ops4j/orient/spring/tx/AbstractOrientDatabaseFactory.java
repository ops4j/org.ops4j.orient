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

import javax.annotation.PostConstruct;

import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;

/**
 * Abstract base class for OrientDB factories. Each concrete implementation is responsible for a
 * given OrientDB type, i.e. document, object or graph database. Concrete implementations also
 * encapsulate a pool for the given database type.
 * <p>
 * All properties are optional and have default values, except the {@code url} property.
 * 
 * @author Harald Wellmann
 * 
 */
public abstract class AbstractOrientDatabaseFactory {

    /** Default database username. */
    public static final String DEFAULT_USERNAME = "admin";

    /** Default database password. */
    public static final String DEFAULT_PASSWORD = "admin";

    /** Default minimum pool size. */
    public static final int DEFAULT_MIN_POOL_SIZE = 1;

    /** Default maximum pool size. */
    public static final int DEFAULT_MAX_POOL_SIZE = 20;

    private String username = DEFAULT_USERNAME;

    private String password = DEFAULT_PASSWORD;

    private int minPoolSize = DEFAULT_MIN_POOL_SIZE;

    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    private String url;

    @PostConstruct
    public void init() {
        if (url == null) {
            throw new IllegalArgumentException("url property must not be null");
        }
        ODatabaseComplex<?> db = newDatabase();
        createDatabase(db);
        createPool();
    }

    /**
     * Creates a database pool.
     */
    protected abstract void createPool();

    /**
     * Gets a new database object from the pool. The returned database is open.
     * 
     * @return database object
     */
    public abstract ODatabaseComplex<?> openDatabase();

    /**
     * Creates a new transactional database object for the URL set on this factory.
     * 
     * @return
     */
    protected abstract ODatabaseComplex<?> newDatabase();

    /**
     * Returns the current database object for the current thread.
     * 
     * @return current database object
     */
    public ODatabaseComplex<?> db() {
        return ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner();
    }

    /**
     * Physically creates a database in the underlying storage. The returned database is closed,
     * except when the database is of type {@code memory}, since a closed memory database cannot be
     * reopened.
     * 
     * @param db database object
     */
    protected void createDatabase(ODatabaseComplex<?> db) {
        if (getUrl().startsWith("memory:")) {
            if (!db.exists()) {
                db.create();
            }
        }
        else if (getUrl().startsWith("local:")) {
            if (!db.exists()) {
                db.create();
                db.close();
            }
        }
    }

    /**
     * @return Database URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the database URL for the database objects produced by this factory. The URL
     * <em>must</em> be set before invoking any non-accessor method of this factory.
     * 
     * @param url database URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the database username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the database username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the database password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the database password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the minimum pool size.
     * 
     * @return the minPoolSize
     */
    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * Sets the minimum pool size.
     * 
     * @param minPoolSize the minPoolSize to set
     */
    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    /**
     * Gets the maximum pool size.
     * 
     * @return the maxPoolSize
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets the maximum pool size.
     * 
     * @param maxPoolSize the maxPoolSize to set
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
