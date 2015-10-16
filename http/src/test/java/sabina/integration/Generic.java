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

package sabina.integration;

import static org.testng.Assert.*;
import static sabina.integration.TestScenario.*;
import static java.lang.String.*;

import sabina.Request;
import sabina.Router.VoidHandler;
import sabina.Server;

final class Generic {
    private static String part = "param";

    static void setup (Server s) {
        s.before ("/protected/*", it -> it.halt (401, "Go Away!"));

        s.get ("/request/data", it -> {
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

        s.exception (
            UnsupportedOperationException.class,
            (ex, req) -> req.response.header ("error", ex.getMessage ())
        );

        s.get ("/exception", (VoidHandler)it -> {
            throw new UnsupportedOperationException ("error message");
        });

        s.get ("/hi", it -> "Hello World!");

        s.get ("/param/:param", it -> "echo: " + it.params (":param"));

        s.get ("/paramandwild/:param/stuff/*", it ->
                "paramandwild: " + it.params (":param") + it.splat ().get (0)
        );

        s.get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        s.get ("/", it -> "Hello Root!");

        s.post ("/poster", it -> {
            String body = it.body ();
            it.response.status (201); // created
            return "Body was: " + body;
        });

        s.patch ("/patcher", it -> {
            String body = it.body ();
            it.response.status (200);
            return "Body was: " + body;
        });

//        s.delete ("/method", Request::requestMethod);
//        s.options ("/method", Request::requestMethod);
//        s.get ("/method", Request::requestMethod);
//        s.patch ("/method", Request::requestMethod);
//        s.post ("/method", Request::requestMethod);
//        s.put ("/method", Request::requestMethod);
//        s.trace ("/method", Request::requestMethod);
//        s.head ("/method", it -> {
//            it.header ("header", it.requestMethod ());
//        });

        s.get ("/halt", it -> {
            it.halt (500, "halted");
        });

        s.get ("/tworoutes/" + part + "/:param", it ->
                part + " route: " + it.params (":param")
        );

        s.get ("/tworoutes/" + part.toUpperCase () + "/:param", it ->
                part.toUpperCase () + " route: " + it.params (":param")
        );

        s.get ("/reqres", Request::requestMethod);

        s.after ("/hi", it -> it.response.header ("after", "foobar"));
    }

    static void reqres (TestScenario scenario) {
        UrlResponse response = scenario.doMethod ("GET", "/reqres");
        scenario.assertResponseEquals (response, "GET", 200);
    }

    static void getHi (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/hi");
        testScenario.assertResponseEquals (response, "Hello World!", 200);
    }

    static void hiHead (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("HEAD", "/hi");
        testScenario.assertResponseEquals (response, "", 200);
    }

    static void getHiAfterFilter (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    static void getRoot (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/");
        testScenario.assertResponseEquals (response, "Hello Root!", 200);
    }

    static void paramAndWild (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/paramandwild/thedude/stuff/andits");
        testScenario.assertResponseEquals (response, "paramandwild: thedudeandits", 200);
    }

    static void echoParam1 (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/param/shizzy");
        testScenario.assertResponseEquals (response, "echo: shizzy", 200);
    }

    static void echoParam2 (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/param/gunit");
        testScenario.assertResponseEquals (response, "echo: gunit", 200);
    }

    static void echoParamWithUpperCaseInValue (TestScenario testScenario) {
        final String camelCased = "ThisIsAValueAndSabinaShouldRetainItsUpperCasedCharacters";
        UrlResponse response = testScenario.doMethod ("GET", "/param/" + camelCased);
        testScenario.assertResponseEquals (response, "echo: " + camelCased, 200);
    }

    static void twoRoutesWithDifferentCase (TestScenario testScenario) {
        String expected = "expected";
        UrlResponse response1 = testScenario.doMethod ("GET", "/tworoutes/" + part + "/" + expected);
        testScenario.assertResponseEquals (response1, part + " route: " + expected, 200);

        expected = expected.toUpperCase ();
        UrlResponse response =
            testScenario.doMethod ("GET", "/tworoutes/" + part.toUpperCase () + "/" + expected);
        testScenario.assertResponseEquals (response, part.toUpperCase () + " route: " + expected, 200);
    }

    static void echoParamWithMaj (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/paramwithmaj/plop");
        testScenario.assertResponseEquals (response, "echo: plop", 200);
    }

    static void unauthorized (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/protected/resource");
        assertTrue (response.status == 401);
    }

    static void notFound (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/no/resource");
        assertTrue (response.status == 404);
    }

	static void fileNotFound (TestScenario testScenario) {
		UrlResponse response = testScenario.doMethod ("GET", "/resource.html");
		assertTrue (response.status == 404);
	}

    static void postOk (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("POST", "/poster", "Fo shizzy");
        testScenario.assertResponseContains (response, "Fo shizzy", 201);
    }

    static void patchOk (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("PATCH", "/patcher", "Fo shizzy");
        testScenario.assertResponseContains (response, "Fo shizzy", 200);
    }

    static void staticFile (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/css/style.css");
        if (testScenario.externalFiles)
            testScenario.assertResponseEquals (response, "/*\n * Content of css file\n */\n", 200);
        else
            assert response.status == 404;
    }

    static void externalStaticFile (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/externalFile.html");
        if (testScenario.externalFiles)
            testScenario.assertResponseEquals (response, "Content of external file", 200);
        else
            assert response.status == 404;
    }

    static void halt (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/halt");
        testScenario.assertResponseEquals (response, "halted", 500);
    }

    // TODO Check with asserts
    static void requestData (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/request/data?query");
        String port = String.valueOf (testScenario.port);
        String protocol = testScenario.secure? "https" : "http";

//        assertEquals ("error message", response.cookies.get ("method"));
//        assertEquals ("error message", response.cookies.get ("host"));
//        assertEquals ("error message", response.cookies.get ("uri"));
//        assertEquals ("error message", response.cookies.get ("params"));

        assertEquals ("Apache-HttpClient/4.3.3 (java 1.5)", response.headers.get ("agent"));
        assertEquals ("HTTP/1.1", response.headers.get ("protocol"));
        assertEquals (protocol, response.headers.get ("scheme"));
//        assertEquals ("localhost", response.headers.get ("host"));
        assertEquals ("query", response.headers.get ("query"));
        assertEquals (port, response.headers.get ("port"));

        assertEquals (response.body, format ("%s://localhost:%s/request/data!!!", protocol, port));
        assertEquals (200, response.status);
    }

    static void handleException (TestScenario testScenario) {
        UrlResponse response = testScenario.doMethod ("GET", "/exception");
        assertEquals ("error message", response.headers.get ("error"));
    }

//    static void methods (TestScenario testScenario) {
//        checkMethod (testScenario, "HEAD", "header"); // Head does not support body message
//        checkMethod (testScenario, "DELETE");
//        checkMethod (testScenario, "OPTIONS");
//        checkMethod (testScenario, "GET");
//        checkMethod (testScenario, "PATCH");
//        checkMethod (testScenario, "POST");
//        checkMethod (testScenario, "PUT");
//        checkMethod (testScenario, "TRACE");
//    }
//
//    private static void checkMethod (TestScenario testScenario, String methodName) {
//        checkMethod (testScenario, methodName, null);
//    }
//
//    private static void checkMethod (TestScenario testScenario, String methodName, String headerName) {
//        UrlResponse res = testScenario.doMethod (methodName, "/method");
//        assertEquals (headerName == null? res.body : res.headers.get (headerName), methodName);
//        assertEquals (200, res.status);
//    }
}
