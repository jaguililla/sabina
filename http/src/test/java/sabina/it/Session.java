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

package sabina.it;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static sabina.Sabina.*;
import static sabina.util.TestUtil.*;

import java.util.stream.Collectors;

import sabina.util.TestUtil;

/**
 * TODO .
 *
 * @author jam
 */
public class Session {
    private static TestUtil testUtil = new TestUtil ();

    public static void setup () throws InterruptedException {
        put ("/session/:key/:value", it -> {
            it.session ().attribute (it.params (":key"), it.params (":value"));
            return null;
        });

        get ("/session/:key/", it -> it.session ().attribute (it.splat ()[0]));
        delete ("/session/:key/", it -> {
            it.session ().removeAttribute (it.params (":key"));
            return null;
        });

        get ("/session/", it ->
                it.session ().attributes ().stream ().collect (Collectors.joining ())
        );
    }

    public void attribute () {
        UrlResponse res = testUtil.doPut ("/session/foo/bar");

        assertNotNull (res);
        assertEquals (200, res.status);

        res = testUtil.doGet ("/session/foo");

        assertNotNull (res);
        assertNotNull (res.body);
        assertEquals (res.body, "bar");
        assertEquals (200, res.status);
    }
}
