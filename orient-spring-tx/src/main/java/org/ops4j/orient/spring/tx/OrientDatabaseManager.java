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
import javax.annotation.PreDestroy;

import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;

/**
 * @author Harald Wellmann
 * 
 */
public class OrientDatabaseManager {

    private String url;

    private String type;

    private String username;

    private String password;

    private ODatabaseComplex<?> db;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
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
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public ODatabaseComplex getDatabase() {
        return (ODatabaseComplex) db;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        if (type.equals("object")) {
            db = OObjectDatabasePool.global().acquire(url, username, password);
        }
        else if (type.equals("document")) {
            // ODatabaseDocumentTx db = new ODatabaseDocumentTx(url);
            // this.db = db;
            // if (!db.exists()) {
            // db.create();
            // // db.close();
            // }
            // else {
            // // db = ODatabaseDocumentPool.global().acquire(url, username, password);
            // db.open(username, password);
            // }

            ODatabaseDocumentTx db = new ODatabaseDocumentTx(url);
            this.db = db;
            if (!db.exists()) {
                db.create();
            }
            else {
                db.open(username, password);
            }
            db = ODatabaseDocumentPool.global().acquire(url, username, password);
        }
        else if (type.equals("graph")) {
            db = OGraphDatabasePool.global().acquire(url, username, password);
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        db.close();
    }

}
