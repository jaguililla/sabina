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

package sabina.examples;

import static sabina.Route.contentType;
import static sabina.Route.get;
import static sabina.Route.path;
import static sabina.Server.serve;

class ServeHelloWorld {
    public static void main (String[] args) {
        serve (
            get ("hello", it -> "Hello World!"),
            path ("hi",
                path ("Joe", i -> i.response.body ("Joe")),
                get ("Jane", j -> "Jane"),
                contentType ("text/xml",
                    get (z -> "a")
                )
            )
        );
    }
}
