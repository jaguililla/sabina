/*
 * Copyright © 2011 Per Wendel. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.it;

import static java.lang.System.getProperty;
import static java.lang.System.out;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static sabina.Server.*;
import static sabina.util.TestUtil.UrlResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.Server;
import sabina.util.TestUtil;

public class GenericIT {

    private static TestUtil testUtil = new TestUtil ();
    private static File tmpExternalFile;
    private static Server server;

    @AfterClass public static void shutDown () {
        server.stop ();
        testUtil.waitForShutdown ();
        if (tmpExternalFile != null)
            tmpExternalFile.delete ();
    }

    @BeforeClass public static void startUp () throws IOException, InterruptedException {
        tmpExternalFile = new File (getProperty ("java.io.tmpdir"), "externalFile.html");

        FileWriter writer = new FileWriter (tmpExternalFile);
        writer.write ("Content of external file");
        writer.flush ();
        writer.close ();

        server = server (
            before ("/protected/*", it -> it.halt (401, "Go Away!")),

            before ("/protected/*", "application/json", it ->
                    it.halt (401, "{\"message\": \"Go Away!\"}")
            ),

            get ("/hi", "application/json", it -> "{\"message\": \"Hello World\"}"),

            get ("/hi", it -> "Hello World!"),

            get ("/param/:param", it -> "echo: " + it.params (":param")),

            get ("/paramandwild/:param/stuff/*", it ->
                    "paramandwild: " + it.params (":param") + it.splat ()[0]
            ),

            get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj")),

            get ("/", it -> "Hello Root!"),

            post ("/poster", it -> {
                String body = it.requestBody ();
                it.status (201); // created
                return "Body was: " + body;
            }),

            patch ("/patcher", it -> {
                String body = it.requestBody ();
                it.status (200);
                return "Body was: " + body;
            }),

            after ("/hi", it -> it.header ("after", "foobar"))
        );

        server.setPort (testUtil.getPort ());
        server.staticFileLocation ("/public");
        server.externalStaticFileLocation (getProperty ("java.io.tmpdir"));
        server.startUp ();

        testUtil.waitForStartup ();
    }

    @Test public void filtersShouldBeAcceptTypeAware () throws Exception {
        UrlResponse response =
            testUtil.doMethod ("GET", "/protected/resource", null, "application/json");
        assertTrue (response.status == 401);
        assertEquals ("{\"message\": \"Go Away!\"}", response.body);
    }

    @Test public void routesShouldBeAcceptTypeAware () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/hi", null, "application/json");
        assertEquals (200, response.status);
        assertEquals ("{\"message\": \"Hello World\"}", response.body);
    }

    @Test public void getHi () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void hiHead () {
        UrlResponse response = testUtil.doMethod ("HEAD", "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void getHiAfterFilter () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void getRoot () {
        UrlResponse response = testUtil.doMethod ("GET", "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void paramAndWild () {
        UrlResponse response =
            testUtil.doMethod ("GET", "/paramandwild/thedude/stuff/andits");
        assertEquals (200, response.status);
        assertEquals ("paramandwild: thedudeandits", response.body);
    }

    @Test public void echoParam1 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void echoParam2 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void echoParamWithUpperCaseInValue () {
        final String camelCased = "ThisIsAValueAndSabinaShouldRetainItsUpperCasedCharacters";
        UrlResponse response = testUtil.doMethod ("GET", "/param/" + camelCased);
        assertEquals (200, response.status);
        assertEquals ("echo: " + camelCased, response.body);
    }

    @Test (enabled=false) public void twoRoutesWithDifferentCaseButSameName () {
        String lowerCasedRoutePart = "param";
        String uppperCasedRoutePart = "PARAM";

        registerEchoRoute (lowerCasedRoutePart);
        registerEchoRoute (uppperCasedRoutePart);
        assertEchoRoute (lowerCasedRoutePart);
        assertEchoRoute (uppperCasedRoutePart);
    }

    private static void registerEchoRoute (final String routePart) {
        get ("/tworoutes/" + routePart + "/:param", it ->
            routePart + " route: " + it.request.params (":param")
        );
    }

    private static void assertEchoRoute (String routePart) {
        final String expected = "expected";
        UrlResponse response =
            testUtil.doMethod ("GET", "/tworoutes/" + routePart + "/" + expected);
        assertEquals (200, response.status);
        assertEquals (routePart + " route: " + expected, response.body);
    }

    @Test public void echoParamWithMaj () {
        UrlResponse response = testUtil.doMethod ("GET", "/paramwithmaj/plop");
        assertEquals (200, response.status);
        assertEquals ("echo: plop", response.body);
    }

    @Test public void unauthorized () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/protected/resource");
        assertTrue (response.status == 401);
    }

    @Test public void notFound () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/no/resource");
        assertTrue (response.status == 404);
    }

	@Test public void fileNotFound () throws Exception {
		UrlResponse response = testUtil.doMethod ("GET", "/resource.html");
		assertTrue (response.status == 404);
	}

    @Test public void postOk () {
        UrlResponse response = testUtil.doMethod ("POST", "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void patchOk () {
        UrlResponse response = testUtil.doMethod ("PATCH", "/patcher", "Fo shizzy");
        out.println (response.body);
        assertEquals (200, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void staticFile () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/css/style.css");
        assertEquals (200, response.status);
        assertEquals ("/*\n * Content of css file\n */\n", response.body);
    }

    @Test public void externalStaticFile () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/externalFile.html");
        assertEquals (200, response.status);
        assertEquals ("Content of external file", response.body);
    }
}
