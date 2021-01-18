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

import static org.testng.Assert.assertEquals;
import static sabina.integration.TestScenario.UrlResponse;

import sabina.Request;
import sabina.Server;

/**
 * Routes, content types, methods...
 *
 * @author jam
 */
final class Routes {
    static void ctVoidMethodHandler (Request req) {
        req.header ("header", req.requestMethod ());
        req.header ("contentType", "application/json");
    }

    static Object ctMethodHandler (Request req) {
        req.header ("header", req.requestMethod ());
        req.header ("contentType", "application/json");
        return req.requestMethod ();
    }

    static void voidMethodHandler (Request req) {
        req.header ("header", req.requestMethod ());
    }

    static Object methodHandler (Request req) {
        req.header ("header", req.requestMethod ());
        return req.requestMethod ();
    }

    static void setup (Server s) {
        // Test all route methods
        s.delete ("/routes/method", Routes::methodHandler);
        s.options ("/routes/method", Routes::methodHandler);
        s.get ("/routes/method", Routes::methodHandler);
        s.patch ("/routes/method", Routes::methodHandler);
        s.post ("/routes/method", Routes::methodHandler);
        s.put ("/routes/method", Routes::methodHandler);
        s.trace ("/routes/method", Routes::methodHandler);
        s.head ("/routes/method", Routes::methodHandler);

        s.delete ("/routes/method", Routes::voidMethodHandler);
        s.options ("/routes/method", Routes::voidMethodHandler);
        s.get ("/routes/method", Routes::voidMethodHandler);
        s.patch ("/routes/method", Routes::voidMethodHandler);
        s.post ("/routes/method", Routes::voidMethodHandler);
        s.put ("/routes/method", Routes::voidMethodHandler);
        s.trace ("/routes/method", Routes::voidMethodHandler);
        s.head ("/routes/method", Routes::voidMethodHandler);

        // Specific head method tests

        // Test different accept types (select best one, fall back to default, 404 if no default)
    }

    static void methods (TestScenario testScenario) {
        checkMethod (testScenario, "HEAD", "header"); // Head does not support body message
        checkMethod (testScenario, "DELETE");
        checkMethod (testScenario, "OPTIONS");
        checkMethod (testScenario, "GET");
        checkMethod (testScenario, "PATCH");
        checkMethod (testScenario, "POST");
        checkMethod (testScenario, "PUT");
        checkMethod (testScenario, "TRACE");
    }

    private static void checkMethod (TestScenario testScenario, String methodName) {
        checkMethod (testScenario, methodName, null);
    }

    /**
     * TODO Review this test uncommenting failing line!!!
     * @param testScenario
     * @param methodName
     * @param headerName
     */
    private static void checkMethod (TestScenario testScenario, String methodName, String headerName) {
        UrlResponse res = testScenario.doMethod (methodName, "/routes/method");
        assertEquals (headerName == null? res.body : res.headers.get (headerName), methodName);
        assertEquals (200, res.status);

        res = testScenario.doMethod (methodName, "/routes/method", "", "application/json");
        assertEquals (headerName == null? res.body : res.headers.get (headerName), methodName);
//        assertEquals (res.headers.get ("contentType"), "application/json");
        assertEquals (200, res.status);
    }
}
