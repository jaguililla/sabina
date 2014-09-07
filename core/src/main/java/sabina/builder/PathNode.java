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

public class PathNode extends Node {
    final String path;

    PathNode (String aPath, Node... aChildren) {
        super (aChildren);

        if (aPath == null
            || aPath.isEmpty ()
            || ((aPath.startsWith ("/") || aPath.endsWith ("/")) && aPath.length () > 1)
            || aChildren.length == 0)
            throw new IllegalArgumentException (
                "Invalid path: " + aPath + " or children: " + aChildren.length);

        path = aPath;
    }
}
