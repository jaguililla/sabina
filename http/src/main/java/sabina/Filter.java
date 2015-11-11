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
import static sabina.Request.convertRouteToList;
import static sabina.Router.ALL_PATHS;
import static sabina.util.Checks.checkArgument;

import java.util.List;

import sabina.Router.Handler;

/**
 * @author Per Wendel
 */
public final class Filter {

    public final String path;
    public final FilterOrder order;
    public final Handler handler;

    public final List<String> routeParts;

    /**
     * Constructor.
     *
     * TODO Maybe this is only intended for filters!!!
     *
     * @param order .
     * @param handler .
     */
    public Filter (final FilterOrder order, final Handler handler) {
        this (order, Router.ALL_PATHS, handler);
    }

    /**
     * Constructor.
     *
     * @param order .
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     * @param handler .
     */
    public Filter (final FilterOrder order, final String path, final Handler handler) {
        checkArgument (path != null);
        checkArgument (handler != null && order != null);

        this.path = path;
        this.order = order;
        this.handler = handler;
        this.routeParts = convertRouteToList (path);
    }

    @Override public String toString () {
        return format ("Filter: %s %s", order, path);
    }
}
