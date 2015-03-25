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

package sabina.examples;

import static sabina.Sabina.*;

/**
 * Example to show that a route with an accept type only replies to that content type.
 *
 * @author Per Wendel
 */
final class JsonAcceptTypeExample {
    public static void main (String args[]) {
        /*
		 * Running curl -i -H "Accept: application/json" http://localhost:4567/hello json
		 * message is read.
		 *
		 * Running curl -i -H "Accept: text/html" http://localhost:4567/hello HTTP 404 error is
		 * thrown.
		 */
        get ("/hello", "application/json", it -> "{\"message\": \"Hello World\"}").start ();
    }
}
