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

package sabina.builder;

import static org.testng.Assert.*;
import static sabina.HttpMethod.after;
import static sabina.HttpMethod.get;

import org.testng.annotations.Test;

public class RouteNodeTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void routeNodeNull () {
        new RouteNode (get, null);
    }

    @Test public void routeNode () {
        assertTrue (new RouteNode (after, c -> 0).handler != null);
        assertTrue (new RouteNode (get, c -> 0).handler != null);
    }
}
