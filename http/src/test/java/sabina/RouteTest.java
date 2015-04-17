/*
 * Copyright © 2014 Juan José Aguililla. All rights reserved.
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

package sabina;

import static sabina.HttpMethod.AFTER;

import org.testng.annotations.Test;

public class RouteTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void routeWithNullMethod () {
        new Route (null, "path", "type", it -> "");
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void routeWithNullPath () {
        new Route (AFTER, null, "type", it -> "");
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void routeWithNullAcceptType () {
        new Route (AFTER, "path", null, it -> "");
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void routeWithEmptyPath () {
        new Route (AFTER, "", "type", it -> "");
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void routeWithEmptyAcceptType () {
        new Route (AFTER, "path", "", it -> "");
    }

    @Test public void testToString () throws Exception {
        Route action = new Route (AFTER, "path", "type", it -> "");
        assert action.path.equals ("path");
        assert action.acceptType.equals ("type");
        assert action.method.equals (AFTER);
        assert action.toString ().equals ("AFTER path [type]");
    }
}
