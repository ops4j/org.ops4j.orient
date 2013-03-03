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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Harald Wellmann
 *
 */
@Configuration
@EnableTransactionManagement
public class ObjectSpringTestConfig {
    
    @Bean
    public OrientTransactionManager transactionManager() {
        OrientTransactionManager bean = new OrientTransactionManager();
        bean.setDatabaseManager(databaseFactory());
        return bean;        
    }
    
    @Bean
    public OrientObjectDatabaseFactory databaseFactory() {
        OrientObjectDatabaseFactory manager = new OrientObjectDatabaseFactory();
        //manager.setUrl("local:target/test");
        manager.setUrl("memory:test");
        manager.setUsername("admin");
        manager.setPassword("admin");
        return manager;
    }
    
    
    @Bean
    public TransactionalObjectService transactionalService() {
        return new TransactionalObjectService();
    }
}
