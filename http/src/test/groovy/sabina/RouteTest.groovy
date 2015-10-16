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

package sabina

import static sabina.HttpMethod.AFTER

import org.testng.annotations.Test
import sabina.Router.Handler

public class RouteTest {
    @Test (expectedExceptions = IllegalArgumentException)
    public void routeWithNullMethod () {
        new Route (null, "path", { "" } as Handler)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void routeWithNullPath () {
        new Route (AFTER, null, { "" } as Handler)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void routeWithEmptyPath () {
        new Route (AFTER, "", { "" } as Handler)
    }

    @Test public void testToString () throws Exception {
        Route action = new Route (AFTER, "path", { "" } as Handler)
        assert action.path.equals ("path")
        assert action.method.equals (AFTER)
        assert action.toString ().equals ("Route: AFTER path")
    }
}
