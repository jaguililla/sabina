/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
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

import static java.util.stream.Collectors.joining;
import static org.testng.Assert.assertEquals;
import static sabina.integration.TestScenario.*;

import sabina.Router.Handler;
import sabina.Server;

/**
 * TODO .
 *
 * @author jam
 */
final class Session {
    static void setup (Server s) {
        s.put ("/session/:key/:value", it -> {
            it.session ().attribute (it.params (":key"), it.params (":value"));
        });

        s.get ("/session/:key", (Handler)it -> it.session ().attribute (it.params (":key")));

        s.delete ("/session/:key", it -> {
            it.session ().removeAttribute (it.params (":key"));
        });

        s.get ("/session", (Handler)it ->
            it.session ().attributes ().stream ().collect (joining ())
        );
    }

    static void attribute (TestScenario testScenario) {
        UrlResponse res = testScenario.doPut ("/session/foo/bar");
        assertEquals (200, res.status);

        res = testScenario.doGet ("/session/foo");
        testScenario.assertResponseEquals (res, "bar", 200);
    }
}
