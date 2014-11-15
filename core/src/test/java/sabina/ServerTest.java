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
import static sabina.Route.*;

import org.testng.annotations.Test;
import sabina.builder.Node;

public class ServerTest {
    @Test public void getActions () {
        Node node =
            path ("path",
                contentType ("html",
                    get (con -> 200)
                )
            );

        Server server = new Server ();
        Action action = server.getActions (node).get (0);

        assertEquals (action.path, "/path");
        assertEquals (action.acceptType, "html");
        assertEquals (action.method, HttpMethod.get);
    }
}
