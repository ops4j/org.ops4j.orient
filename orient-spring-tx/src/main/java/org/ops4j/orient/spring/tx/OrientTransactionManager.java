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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * A PlatformTransactionManager for Orient databases, enabling declarative transactions for a single
 * Orient database. This OrientTransactionManager depends on an
 * {@link AbstractOrientDatabaseFactory}.
 * 
 * @author Harald Wellmann
 * 
 */
public class OrientTransactionManager extends AbstractPlatformTransactionManager implements
        ResourceTransactionManager {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(OrientTransactionManager.class);

    private AbstractOrientDatabaseFactory dbf;

    @Override
    protected Object doGetTransaction() throws TransactionException {
        OrientTransaction tx = new OrientTransaction();

        ODatabaseComplex<?> db = (ODatabaseComplex<?>) TransactionSynchronizationManager
                .getResource(getResourceFactory());
        if (db != null) {
            tx.setDatabase(db);
        }

        return tx;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition)
            throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;

        ODatabaseComplex<?> db = tx.getDatabase();
        if (db == null || db.isClosed()) {
            boolean closed = (db != null);
            db = dbf.openDatabase();
            tx.setDatabase(db);
            ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseRecord) db.getUnderlying());
            if (closed) {
                TransactionSynchronizationManager.unbindResource(dbf);
            }
            TransactionSynchronizationManager.bindResource(dbf, db);
        }
        log.debug("beginning transaction on db.hashCode() = {}", db.hashCode());
        db.begin();
        tx.setTx(db.getTransaction());
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) status.getTransaction();
        ODatabaseComplex<?> db = tx.getDatabase();
        log.debug("committing transaction on db.hashCode() = {}", db.hashCode());
        db.commit();
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) status.getTransaction();
        ODatabaseComplex<?> db = tx.getDatabase();
        log.debug("committing transaction on db.hashCode() = {}", db.hashCode());
        db.rollback();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        OrientTransaction tx = (OrientTransaction) transaction;
        if (!tx.getDatabase().isClosed()) {
            tx.getDatabase().close();
        }
    }

    @Override
    public Object getResourceFactory() {
        return dbf;
    }

    /**
     * Gets the database factory for the database managed by this transaction manager.
     * 
     * @return the database
     */
    public AbstractOrientDatabaseFactory getDatabaseFactory() {
        return dbf;
    }

    /**
     * Sets the database factory for the database managed by this transaction manager.
     * 
     * @param databaseFactory the database to set
     */
    public void setDatabaseManager(AbstractOrientDatabaseFactory databaseFactory) {
        this.dbf = databaseFactory;
    }
}
