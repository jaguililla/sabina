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

package sabina.server;

import sabina.route.RouteMatcher;

/**
 * @author Per Wendel
 */
public final class BackendFactory {
    private BackendFactory () {
        throw new IllegalStateException ();
    }

    private static Backend createJetty (RouteMatcher matcher, boolean hasMultipleHandler) {
        return new JettyServer (createFilter ("jetty", matcher, hasMultipleHandler));
    }

    private static MatcherFilter createFilter (
        String backend, RouteMatcher matcher, boolean hasMultipleHandler) {
        return new MatcherFilter (matcher, backend, hasMultipleHandler);
    }

    private static Backend createUndertow (RouteMatcher matcher, boolean hasMultipleHandler) {
        return new UndertowServer (createFilter ("undertow", matcher, hasMultipleHandler));
    }

    public static Backend create (String backend, RouteMatcher matcher, boolean multipleHandlers) {
        switch (backend) {
            case "jetty":
                return createJetty (matcher, multipleHandlers);
            case "undertow":
                return createUndertow (matcher, multipleHandlers);
            default:
                throw new IllegalStateException ();
        }
    }
}
