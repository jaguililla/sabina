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
final class Paths {
    private static String part = "param";

    static void voidMethodHandler (Request req) {
        req.header ("requestedMethod", req.requestMethod ());
    }

    static Object methodHandler (Request req) {
        req.header ("requestedMethod", req.requestMethod ());
        return req.requestMethod ();
    }

    static void setup (Server s) {
        // Test nested routes (check the most specific one is picked, check routes with params). Ie:
        // /a/:b
        // /a/b

        // Test params

        // Test splats
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

    private static void checkMethod (TestScenario testScenario, String methodName, String headerName) {
        UrlResponse res = testScenario.doMethod (methodName, "/method");
        assertEquals (headerName == null? res.body : res.headers.get (headerName), methodName);
        assertEquals (200, res.status);
    }
}
