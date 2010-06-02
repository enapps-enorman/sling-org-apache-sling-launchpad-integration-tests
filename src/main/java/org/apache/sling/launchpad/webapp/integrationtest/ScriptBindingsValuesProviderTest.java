/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.launchpad.webapp.integrationtest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.servlets.post.SlingPostConstants;

public class ScriptBindingsValuesProviderTest extends RenderingTestBase {

    private String slingResourceType;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // set test values
        slingResourceType = "integration-test/srt." + System.currentTimeMillis();
        testText = "This is a test " + System.currentTimeMillis();

        // create the test node, under a path that's specific to this class to allow collisions
        final String url = HTTP_BASE_URL + "/" + getClass().getSimpleName() + "/" + System.currentTimeMillis() + SlingPostConstants.DEFAULT_CREATE_SUFFIX;
        final Map<String,String> props = new HashMap<String,String>();
        props.put("sling:resourceType", slingResourceType);
        props.put("text", testText);
        displayUrl = testClient.createNode(url, props);

        // the rendering script goes under /apps in the repository
        scriptPath = "/apps/" + slingResourceType;
        testClient.mkdirs(WEBDAV_BASE_URL, scriptPath);
    }

    public void testGenericProvider() throws IOException {
        final String toDelete = uploadTestScript("bindingsprovided.esp","html.esp");
        try {
            final String content = getContent(displayUrl + ".html", CONTENT_TYPE_HTML);
            assertTrue("Content includes ESP marker (" + content + ")",content.contains("ESP template"));
            assertTrue("Content includes test text (" + content + ")", content.contains("Hello World!"));
            assertTrue("Content doesn't include Groovy-specific test text (" + content + ")", content.contains("groovyHelloWorld:undefined"));
        } finally {
            testClient.delete(toDelete);
        }
    }

    public void testGroovyProvider() throws IOException {
        final String toDelete = uploadTestScript("bindingsprovided.groovy","html.groovy");
        try {
            final String content = getContent(displayUrl + ".html", CONTENT_TYPE_HTML);
            assertTrue("Content includes ESP marker (" + content + ")",content.contains("ESP template"));
            assertTrue("Content includes test text (" + content + ")", content.contains("Hello World!"));
            assertTrue("Content includes Groovy-specific test text (" + content + ")", content.contains("Hello World from Groovy!"));
        } finally {
            testClient.delete(toDelete);
        }
    }

    public void testJSPProvider() throws IOException {
        final String toDelete = uploadTestScript("bindingsprovided.jsp","html.jsp");
        try {
            final String content = getContent(displayUrl + ".html", CONTENT_TYPE_HTML);
            assertTrue("Content includes JSP marker (" + content + ")",content.contains("bindingsprovided.jsp"));
            assertTrue("Content includes test text (" + content + ")", content.contains("Hello World!"));
            assertTrue("Content includes JSP-specific test text (" + content + ")", content.contains("Hello World from JSP!"));
            assertFalse("Content doesn't includes Groovy-specific test text (" + content + ")", content.contains("Hello World from Groovy!"));
        } finally {
            testClient.delete(toDelete);
        }
    }

    public void testJSPProviderEL() throws IOException {
        final String toDelete = uploadTestScript("bindingsprovided_el.jsp","html.jsp");
        try {
            final String content = getContent(displayUrl + ".html", CONTENT_TYPE_HTML);
            assertTrue("Content includes JSP marker (" + content + ")",content.contains("bindingsprovided_el.jsp"));
            assertTrue("Content includes test text (" + content + ")", content.contains("Hello World!"));
            assertTrue("Content includes JSP-specific test text (" + content + ")", content.contains("Hello World from JSP!"));
            assertFalse("Content doesn't includes Groovy-specific test text (" + content + ")", content.contains("Hello World from Groovy!"));
        } finally {
            testClient.delete(toDelete);
        }
    }

}
