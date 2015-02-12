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

package sabina.route;

import static sabina.Filter.ALL_PATHS;
import static sabina.HttpMethod.after;
import static sabina.HttpMethod.before;
import static sabina.Request.convertRouteToList;

import java.util.List;

import sabina.HttpMethod;

/**
 * @author Per Wendel
 */
final class RouteEntry {
    HttpMethod httpMethod;
    String path;
    String acceptedType;
    Object target;

    boolean matches (HttpMethod httpMethod, String path) {
        return
            ( (httpMethod == before || httpMethod == after)
                && (this.httpMethod == httpMethod)
                && this.path.equals (ALL_PATHS) )
            || ( this.httpMethod == httpMethod
                && matchPath (path) );
    }

    private boolean matchPath (String path) {
        if (!this.path.endsWith ("*") && ((path.endsWith ("/") && !this.path.endsWith ("/"))
            || (this.path.endsWith ("/") && !path.endsWith ("/")))) {
            // One and not both ends with slash
            return false;
        }
        if (this.path.equals (path)) {
            // Paths are the same
            return true;
        }

        // check params
        List<String> thisPathList = convertRouteToList (this.path);
        List<String> pathList = convertRouteToList (path);

        int thisPathSize = thisPathList.size ();
        int pathSize = pathList.size ();

        if (thisPathSize == pathSize) {
            for (int i = 0; i < thisPathSize; i++) {
                String thisPathPart = thisPathList.get (i);
                String pathPart = pathList.get (i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals ("*") && this.path
                    .endsWith ("*"))) {
                    // wildcard match
                    return true;
                }

                if ((!thisPathPart.startsWith (":"))
                    && !thisPathPart.equals (pathPart)
                    && !thisPathPart.equals ("*")) {
                    return false;
                }
            }
            // All parts matched
            return true;
        }
        else {
            // Number of "path parts" not the same
            // check wild card:
            if (this.path.endsWith ("*")) {
                if (pathSize == (thisPathSize - 1) && (path.endsWith ("/"))) {
                    // Hack for making wildcards work with trailing slash
                    pathList.add ("");
                    pathList.add ("");
                    pathSize += 2;
                }

                if (thisPathSize < pathSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = thisPathList.get (i);
                        String pathPart = pathList.get (i);
                        if (thisPathPart.equals ("*") && (i == thisPathSize - 1) && this.path
                            .endsWith ("*")) {
                            // wildcard match
                            return true;
                        }
                        if (!thisPathPart.startsWith (":")
                            && !thisPathPart.equals (pathPart)
                            && !thisPathPart.equals ("*")) {
                            return false;
                        }
                    }
                    // All parts matched
                    return true;
                }
                // End check wild card
            }
            return false;
        }
    }

    public String toString () {
        return httpMethod.name () + ", " + path + ", " + target;
    }
}
