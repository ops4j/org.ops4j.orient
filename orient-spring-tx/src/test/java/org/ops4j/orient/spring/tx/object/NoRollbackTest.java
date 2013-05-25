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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.ops4j.orient.spring.tx.OrientObjectDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This test verifies transaction propagation and rollback behaviour. The test class is not
 * transactional, so the test methods use real transactions at service level.
 * <p>
 * The methods of this test class need to be executed in the given order
 * 
 * @author Harald Wellmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ObjectSpringTestConfig.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoRollbackTest {

    private static boolean dbCleared;

    @Autowired
    private TransactionalObjectService service;

    @Autowired
    private OrientObjectDatabaseFactory dbf;

    @Before
    public void setUp() {
        service.registerEntityClasses();
        if (!dbCleared) {
            service.clear();
            dbCleared = true;
        }
    }

    @Test
    public void test01DbShouldBeEmpty() {
        assertThat(service.countByQuery(), is(0L));
    }

    @Test
    public void test02SaveOneObject() {
        assertThat(service.countByQuery(), is(0L));
        service.commitAutomatically();
        assertThat(service.countByQuery(), is(1L));
    }

    @Test
    public void test03DbShouldNotBeEmpty() {
        assertThat(service.countByQuery(), is(1L));
    }
}
