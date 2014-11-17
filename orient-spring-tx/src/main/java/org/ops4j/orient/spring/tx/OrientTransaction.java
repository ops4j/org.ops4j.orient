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

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.tx.OTransaction;

/**
 * Wrapper for an OrientDB transaction and the associated database.
 * 
 * @author Harald Wellmann
 * 
 */
public class OrientTransaction {

    private OTransaction tx;

    private ODatabase<?> database;

    /**
     * @return the tx
     */
    public OTransaction getTx() {
        return tx;
    }

    /**
     * @param tx
     *            the tx to set
     */
    public void setTx(OTransaction tx) {
        this.tx = tx;
    }

    /**
     * @return the database
     */
    public ODatabase<?> getDatabase() {
        return database;
    }

    /**
     * @param database
     *            the database to set
     */
    public void setDatabase(ODatabase<?> database) {
        this.database = database;
    }
}
