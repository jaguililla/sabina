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
import static org.testng.Assert.*;
import static sabina.Sabina.*;
import static sabina.util.TestUtil.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sabina.Request;
import sabina.util.TestUtil;

public class Generic {

    public static TestUtil testUtil = new TestUtil ();
    private static File tmpExternalFile;

    public static void setupFile () throws IOException {
        tmpExternalFile = new File (getProperty ("java.io.tmpdir"), "externalFile.html");

        FileWriter writer = new FileWriter (tmpExternalFile);
        writer.write ("Content of external file");
        writer.flush ();
        writer.close ();
    }

    public static void cleanupFile () {
        if (tmpExternalFile != null)
            if (!tmpExternalFile.delete ())
                throw new IllegalStateException ();
    }

    public static void setup () {
        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        before ("/protected/*", "application/json", it ->
            it.halt (401, "{\"message\": \"Go Away!\"}")
        );

        get ("/request/data", it -> {
            it.response.body (it.url ());

            it.cookie ("method", it.requestMethod ());
            it.cookie ("host", it.ip ());
            it.cookie ("uri", it.uri ());
            it.cookie ("params", String.valueOf (it.params ().size ()));

            it.header ("agent", it.userAgent ());
            it.header ("protocol", it.protocol ());
            it.header ("scheme", it.scheme ());
            it.header ("host", it.host ());
            it.header ("query", it.queryString ());
            it.header ("port", String.valueOf (it.port ()));

            return it.response.body () + "!!!";
        });

        exception (
            UnsupportedOperationException.class,
            (ex, req) -> req.response.header ("error", ex.getMessage ())
        );

        get ("/exception", it -> { throw new UnsupportedOperationException ("error message"); });

        get ("/hi", "application/json", it -> "{\"message\": \"Hello World\"}");

        get ("/hi", it -> "Hello World!");

        get ("/param/:param", it -> "echo: " + it.params (":param"));

        get ("/paramandwild/:param/stuff/*", it ->
            "paramandwild: " + it.params (":param") + it.splat ()[0]
        );

        get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.body ();
            it.response.status (201); // created
            return "Body was: " + body;
        });

        patch ("/patcher", it -> {
            String body = it.body ();
            it.response.status (200);
            return "Body was: " + body;
        });

        delete ("/method", Request::requestMethod);
        options ("/method", Request::requestMethod);
        get ("/method", Request::requestMethod);
        patch ("/method", Request::requestMethod);
        post ("/method", Request::requestMethod);
        put ("/method", Request::requestMethod);
        trace ("/method", Request::requestMethod);
        head ("/method", it -> {
            it.header ("header", it.requestMethod ());
        });

        get ("/halt", it -> {
            it.halt (500, "halted");
            return "";
        });

        after ("/hi", it -> it.response.header ("after", "foobar"));

        staticFileLocation ("/public");
        externalStaticFileLocation (getProperty ("java.io.tmpdir"));
    }

    public static void filtersShouldBeAcceptTypeAware () {
        UrlResponse response =
            testUtil.doMethod ("GET", "/protected/resource", null, "application/json");
        testUtil.assertResponseEquals (response, "{\"message\": \"Go Away!\"}", 401);
    }

    public static void routesShouldBeAcceptTypeAware () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi", null, "application/json");
        testUtil.assertResponseEquals (response, "{\"message\": \"Hello World\"}", 200);
    }

    public static void getHi () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi");
        testUtil.assertResponseEquals (response, "Hello World!", 200);
    }

    public static void hiHead () {
        UrlResponse response = testUtil.doMethod ("HEAD", "/hi");
        testUtil.assertResponseEquals (response, "", 200);
    }

    public static void getHiAfterFilter () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    public static void getRoot () {
        UrlResponse response = testUtil.doMethod ("GET", "/");
        testUtil.assertResponseEquals (response, "Hello Root!", 200);
    }

    public static void paramAndWild () {
        UrlResponse response =
            testUtil.doMethod ("GET", "/paramandwild/thedude/stuff/andits");
        testUtil.assertResponseEquals (response, "paramandwild: thedudeandits", 200);
    }

    public static void echoParam1 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/shizzy");
        testUtil.assertResponseEquals (response, "echo: shizzy", 200);
    }

    public static void echoParam2 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/gunit");
        testUtil.assertResponseEquals (response, "echo: gunit", 200);
    }

    public static void echoParamWithUpperCaseInValue () {
        final String camelCased = "ThisIsAValueAndSabinaShouldRetainItsUpperCasedCharacters";
        UrlResponse response = testUtil.doMethod ("GET", "/param/" + camelCased);
        testUtil.assertResponseEquals (response, "echo: " + camelCased, 200);
    }

    public static void twoRoutesWithDifferentCase () {
        String lowerCasedRoutePart = "param";
        String uppperCasedRoutePart = "PARAM";

        registerEchoRoute (lowerCasedRoutePart);
        registerEchoRoute (uppperCasedRoutePart);
        assertEchoRoute (lowerCasedRoutePart);
        assertEchoRoute (uppperCasedRoutePart);
    }

    private static void registerEchoRoute (final String routePart) {
        get ("/tworoutes/" + routePart + "/:param", it ->
            routePart + " route: " + it.params (":param")
        );
    }

    private static void assertEchoRoute (String routePart) {
        final String expected = "expected";
        UrlResponse response =
            testUtil.doMethod ("GET", "/tworoutes/" + routePart + "/" + expected);
        testUtil.assertResponseEquals (response, routePart + " route: " + expected, 200);
    }

    public static void echoParamWithMaj () {
        UrlResponse response = testUtil.doMethod ("GET", "/paramwithmaj/plop");
        testUtil.assertResponseEquals (response, "echo: plop", 200);
    }

    public static void unauthorized () {
        UrlResponse response = testUtil.doMethod ("GET", "/protected/resource");
        assertTrue (response.status == 401);
    }

    public static void notFound () {
        UrlResponse response = testUtil.doMethod ("GET", "/no/resource/found");
        assertTrue (response.status == 404);
    }

	public static void fileNotFound () {
		UrlResponse response = testUtil.doMethod ("GET", "/resource.html");
		assertTrue (response.status == 404);
	}

    public static void postOk () {
        UrlResponse response = testUtil.doMethod ("POST", "/poster", "Fo shizzy");
        testUtil.assertResponseContains (response, "Fo shizzy", 201);
    }

    public static void patchOk () {
        UrlResponse response = testUtil.doMethod ("PATCH", "/patcher", "Fo shizzy");
        testUtil.assertResponseContains (response, "Fo shizzy", 200);
    }

    public static void staticFile () {
        UrlResponse response = testUtil.doMethod ("GET", "/css/style.css");
        testUtil.assertResponseEquals (response, "/*\n * Content of css file\n */\n", 200);
    }

    public static void externalStaticFile () {
        UrlResponse response = testUtil.doMethod ("GET", "/externalFile.html");
        testUtil.assertResponseEquals (response, "Content of external file", 200);
    }

    public static void halt () {
        UrlResponse response = testUtil.doMethod ("GET", "/halt");
        testUtil.assertResponseEquals (response, "halted", 500);
    }

    // TODO Check with asserts
    public static void requestData () {
        UrlResponse response = testUtil.doMethod ("GET", "/request/data?query");

//        assertEquals ("error message", response.cookies.get ("method"));
//        assertEquals ("error message", response.cookies.get ("host"));
//        assertEquals ("error message", response.cookies.get ("uri"));
//        assertEquals ("error message", response.cookies.get ("params"));

        assertEquals ("Apache-HttpClient/4.3.3 (java 1.5)", response.headers.get ("agent"));
        assertEquals ("HTTP/1.1", response.headers.get ("protocol"));
        assertEquals ("http", response.headers.get ("scheme"));
//        assertEquals ("localhost", response.headers.get ("host"));
        assertEquals ("query", response.headers.get ("query"));
        assertEquals ("4567", response.headers.get ("port"));

        assertEquals (response.body, "http://localhost:4567/request/data!!!");
        assertEquals (200, response.status);
    }

    public static void handleException () {
        UrlResponse response = testUtil.doMethod ("GET", "/exception");
        assertEquals ("error message", response.headers.get ("error"));
    }

    // TODO Check why HEAD is returning 404
    public static void methods () {
        checkMethod ("HEAD", "header", 404); // Head does not support body message
        checkMethod ("DELETE");
        checkMethod ("OPTIONS");
        checkMethod ("GET");
        checkMethod ("PATCH");
        checkMethod ("POST");
        checkMethod ("PUT");
        checkMethod ("TRACE");
    }

    private static void checkMethod (String methodName) {
        checkMethod (methodName, null, 200);
    }

    private static void checkMethod (String methodName, String headerName, int status) {
        UrlResponse res = testUtil.doMethod (methodName, "/method");
        assertEquals (headerName == null? res.body : res.headers.get (headerName), methodName);
        assertEquals (status, res.status);
    }
}
