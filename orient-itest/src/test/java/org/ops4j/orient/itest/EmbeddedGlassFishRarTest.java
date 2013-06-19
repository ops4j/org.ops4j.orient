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

package org.ops4j.orient.itest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandRunner;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Harald Wellmann
 *
 */
public class EmbeddedGlassFishRarTest {
    
    private static Logger log = LoggerFactory.getLogger(EmbeddedGlassFishRarTest.class);
    
    @Test
    public void deployRar() throws GlassFishException, URISyntaxException, IOException {
        
        System.setProperty("java.util.logging.config.file",
            "src/test/resources/glassfish-config/logging.properties");

        GlassFishProperties gfProps = new GlassFishProperties();
        gfProps.setPort("http-listener", 18080);
        GlassFish gf = GlassFishRuntime.bootstrap().newGlassFish(gfProps);
        gf.start();

        Deployer deployer = gf.getDeployer();
        for (String appName : deployer.getDeployedApplications()) {
            log.info("undeploying " + appName);
            deployer.undeploy(appName);
        }

        File rar = new File("target/libs/orient-rar.rar");
        String sampleAppName = "orient-rar";

        log.info("deploying " + sampleAppName);
        deployer.deploy(rar.toURI(), "--name", sampleAppName, "--contextroot", sampleAppName);
        
        CommandRunner commandRunner = gf.getCommandRunner();
        CommandResult result = commandRunner.run("create-connector-connection-pool", 
            "--raname",  "orient-rar",  
            "--connectiondefinition", "org.ops4j.orient.adapter.api.OrientDatabaseConnectionFactory",            
            "--property", "type=object:connectionUrl=\"memory\\:test\"", "OrientPool");
        log.info(result.getOutput());

        result = commandRunner.run("create-connector-resource", 
            "--poolname",  "OrientPool", "orient/library");
        log.info(result.getOutput());

        
        File war = new File("target/libs/orient-sample2.war");
        String name = deployer.deploy(war.toURI(), "--name", "orient-sample2", "--contextroot", "orient-sample2");
        assertThat(name, is("orient-sample2"));
        
        log.info("undeploying " + name);
        deployer.undeploy(name);
        
        log.info("undeploying " + sampleAppName);
        deployer.undeploy(sampleAppName, "--cascade", "true");

        log.info("stopping GlassFish");
        gf.stop();
    }
}
