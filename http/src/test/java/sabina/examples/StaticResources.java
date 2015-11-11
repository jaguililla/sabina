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

import sabina.Server;

/**
 * Example showing how to serve static resources.
 *
 * @author Per Wendel
 */
final class StaticResources extends Server {
    StaticResources () {
        get ("/hello", it -> "Hello World!");

        /*
         * Will serve all static file are under "/public" in classpath if the route isn't consumed
         * by others routes.
         */
        resourcesLocation ("/public");
        start ();
    }

    public static void main (String[] args) {
        new StaticResources ();
    }
}
