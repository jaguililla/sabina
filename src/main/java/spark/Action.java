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

package spark;

import java.util.regex.Pattern;

import spark.route.HttpMethod;

/**
 * Functionality used in both Route and Filter.
 * TODO Replace optional by Option<String>
 *
 * @author Per Wendel
 */
abstract class Action {
    public final String path;
    public final Pattern pathPattern;
    public final String acceptType;
    public final HttpMethod method;

    /**
     * TODO .
     *
     * @param pathPattern
     * @param acceptType
     */
    protected Action (HttpMethod method, Pattern pathPattern, String acceptType) {
        if (pathPattern == null || acceptType == null || acceptType.isEmpty ())
            throw new IllegalArgumentException ();

        this.pathPattern = pathPattern;
        this.acceptType = acceptType;
        this.path = null;
        this.method = method;
    }

    protected Action (HttpMethod method, String path, String acceptType) {
        if (path == null || path.isEmpty () || acceptType == null || acceptType.isEmpty ())
            throw new IllegalArgumentException ();

        this.path = path;
        this.acceptType = acceptType;
        this.pathPattern = null;
        this.method = method;
    }
}
