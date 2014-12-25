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

import static org.testng.Assert.*;
import static sabina.HttpMethod.after;

import org.testng.annotations.Test;

public class ActionTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void actionWithNullMethod () {
        new Action (null, "path", "type") {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void actionWithNullPath () {
        new Action (after, null, "type") {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void actionWithNullAcceptType () {
        new Action (after, "path", null) {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void actionWithEmptyPath () {
        new Action (after, "", "type") {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void actionWithEmptyAcceptType () {
        new Action (after, "path", "") {};
    }

    @Test public void testToString () throws Exception {
        Action action = new Action (after, "path", "type") {};
        assertEquals (action.path, "path");
        assertEquals (action.acceptType, "type");
        assertEquals (action.method, after);
        assertEquals (action.toString (), "after path [type]");
    }
}
