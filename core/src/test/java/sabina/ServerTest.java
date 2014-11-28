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
import static sabina.Filter.after;
import static sabina.Route.*;

import java.util.List;

import org.testng.annotations.Test;
import sabina.builder.Node;

public class ServerTest {
    private List<Action> getActions (Node... nodes) {
        return new Server ().getActions (nodes);
    }

    private void checkFilter (Action action, HttpMethod method, String path) {
        checkAction (action, method, path, "text/html");
    }

    private void checkRoute (Action action, HttpMethod method, String path) {
        checkAction (action, method, path, "*/*");
    }

    private void checkAction (Action action, HttpMethod method, String path, String type) {
        assertEquals (action.method, method);
        assertEquals (action.path, path);
        assertEquals (action.acceptType, type);
    }

    @Test public void getActionsOneRoute () {
        List<Action> actions = getActions (
            get (con -> 200)
        );

        checkRoute (actions.get (0), HttpMethod.get, "/");
    }

    @Test public void getActionsOneFilter () {
        List<Action> actions = getActions (
            after (con -> {})
        );

        checkFilter (actions.get (0), HttpMethod.after, "/");
    }

    @Test public void getActionsContentType () {
        List<Action> actions = getActions (
            path ("path",
                contentType ("type",
                    get (con -> 200)
                )
            )
        );

        checkAction (actions.get (0), HttpMethod.get, "/path", "type");
    }
}
