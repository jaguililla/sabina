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

package sabina;

import static java.lang.String.format;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Strings.isNullOrEmpty;

/**
 * Functionality used in both Route and Filter.
 *
 * @author Per Wendel
 */
abstract class Action {
    final String path;
    final String acceptType;
    final HttpMethod method;

    Action (final HttpMethod method, final String path, final String acceptType) {
        checkArgument (!isNullOrEmpty (path) && !isNullOrEmpty (acceptType) && method != null);

        this.path = path;
        this.acceptType = acceptType;
        this.method = method;
    }

    @Override public String toString () {
        return format ("%s %s [%s]", method, path, acceptType);
    }
}
