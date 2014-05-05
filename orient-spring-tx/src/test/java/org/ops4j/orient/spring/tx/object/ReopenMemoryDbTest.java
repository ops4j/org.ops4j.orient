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

package org.ops4j.orient.spring.tx.object;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ReopenMemoryDbTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createAndCheckStorageType() {
        OObjectDatabaseTx db = new OObjectDatabaseTx("memory:memtest1");
        db.create();
        assertThat(db.getStorage().getType(), is("memory"));
    }

    @Test
    public void cannotCheckStorageTypeBeforeCreate() {
        OObjectDatabaseTx db = new OObjectDatabaseTx("memory:memtest2");
        assertThat(db.getStorage(), is(nullValue()));
    }

    @Test
    public void cannotOpenCreatedMemoryDb() {
        OObjectDatabaseTx db = new OObjectDatabaseTx("memory:memtest3");
        db.create();

        thrown.expect(ODatabaseException.class);
        thrown.expectCause(isA(IllegalStateException.class));
        db.open("admin", "admin");
    }

    @Test
    public void canReopenClosedMemoryDb() {
        OObjectDatabaseTx db = new OObjectDatabaseTx("memory:memtest4");
        db.create();
        db.close();
        assertThat(db.isClosed(), is(true));
        db.open("admin", "admin");
        assertThat(db.isClosed(), is(false));
        db.close();
    }
}
