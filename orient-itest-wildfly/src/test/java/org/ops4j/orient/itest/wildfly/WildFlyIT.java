/*
 * Copyright 2015 Harald Wellmann.
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
package org.ops4j.orient.itest.wildfly;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.Test;
import org.ops4j.io.StreamUtils;


/**
 * @author Harald Wellmann
 *
 */
public class WildFlyIT {

    @Test
    public void shouldGetBooks() throws IOException {
        URL url = new URL("http://localhost:18080/orient-sample2");
        InputStream is = url.openStream();
        OutputStream os = new ByteArrayOutputStream();
        StreamUtils.copyStream(is, os, true);
        assertThat(os.toString(), containsString("Hobbit"));
    }

}
