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

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.tx.OTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * A PlatformTransactionManager for OrientDB, enabling declarative transactions for a single
 * Orient database. This OrientTransactionManager depends on an
 * {@link AbstractOrientDatabaseFactory}.
 * 
 * @author Harald Wellmann
 * 
 */
public class OrientTransactionManager extends AbstractPlatformTransactionManager implements
        ResourceTransactionManager {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AbstractOrientDatabaseFactory dbf;

    @Override
    protected Object doGetTransaction() throws TransactionException {
        OrientTransaction tx = new OrientTransaction();

        ODatabaseInternal<?> db = (ODatabaseInternal<?>) TransactionSynchronizationManager.getResource(dbf);
        if (db != null) {
            tx.setDatabase(db);
            tx.setTx(db.getTransaction());
        }
        return tx;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;
        boolean condition = tx.getTx() == null ? false : tx.getTx().isActive();
        if(condition){
            logger.debug("\t\\-- *** " +
                    "Participating in running transaction: db.hashCode() = {}", tx.getDatabase().hashCode());
        }else{
            logger.debug("*** isExistingTransaction: No active transaction");
        }

        return condition;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;

        ODatabaseInternal<?> db = tx.getDatabase();
        if (db == null || db.isClosed()) {
            db = dbf.openDatabase();
            tx.setDatabase(db);
            TransactionSynchronizationManager.bindResource(dbf, db);
        }

        if(definition.isReadOnly()) {
            logger.debug("*** Setting transaction to READ-ONLY, db.hashCode() = {}", db.hashCode());
            db.begin(OTransaction.TXTYPE.NOTX);
        }
        logger.debug("*** Beginning transaction, db.hashCode() = {}", db.hashCode());
        db.begin();
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {

        OrientTransaction tx = (OrientTransaction) status.getTransaction();
        ODatabaseInternal<?> db = tx.getDatabase();
        if(status.isReadOnly()){
            logger.debug("*** Committing READ-ONLY transaction - will have no effect, db.hashCode() = {}", db.hashCode());
        }else {
            logger.debug("*** Committing transaction, db.hashCode() = {}", db.hashCode());
        }

    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) status.getTransaction();
        ODatabaseInternal<?> db = tx.getDatabase();

        if(status.isReadOnly()){
            logger.debug("*** Rolling back READ-ONLY transaction - will have no effect, db.hashCode() = {}", db.hashCode());
        }else{
            logger.debug("*** Rolling back transaction, db.hashCode() = {}", db.hashCode());
        }

        db.rollback();
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        logger.debug("*** Marking transaction for rollback");
        status.setRollbackOnly();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        OrientTransaction tx = (OrientTransaction) transaction;
        if (!tx.getDatabase().isClosed()) {
            tx.getDatabase().close();
        }
        TransactionSynchronizationManager.unbindResource(dbf);
        logger.debug("*** Doing cleanup after completion");
    }

    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;
        ODatabaseInternal<?> db = tx.getDatabase();
        return db;
    }

    @Override
    protected void doResume(Object transaction, Object suspendedResources)
            throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;
        ODatabaseInternal<?> db = tx.getDatabase();
        if (!db.isClosed()) {
            db.close();
        }
        ODatabaseInternal<?> oldDb = (ODatabaseInternal<?>) suspendedResources;
        TransactionSynchronizationManager.bindResource(dbf, oldDb);
        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal) oldDb.getUnderlying());
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
