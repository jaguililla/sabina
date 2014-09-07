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

import static sabina.Server.*;

public final class Server {
    public static Server server (Node... aHandler) {
        return server (DEFAULT_PORT, DEFAULT_HOST, aHandler);
    }

    public static Server server (String [] aArgs, Node... aHandler) {
        // TODO Parse args
        return server (DEFAULT_PORT, DEFAULT_HOST, aHandler);
    }

    public static Server server (int aPort, Node... aHandler) {
        return null;
    }

    public static Server server (int aPort, String aHost, Node... aHandler) {
        return null;
    }

    public static void serve (Node... aHandler) {}
    public static void serve (String [] aArgs, Node... aHandler) {}
    public static void serve (int aPort, Node... aHandler) {}
    public static void serve (int aPort, String aHost, Node aHandler) {}

    public static Server secureServer (Node... aHandler) {
        return secureServer (DEFAULT_PORT, DEFAULT_HOST, aHandler);
    }

    public static Server secureServer (String [] aArgs, Node... aHandler) {
        return secureServer (DEFAULT_PORT, DEFAULT_HOST, aHandler);
    }

    public static Server secureServer (int aPort, Node... aHandler) {
        return null;
    }

    public static Server secureServer (int aPort, String aHost, Node... aHandler) {
        return null;
    }

    public static void secureServe (Node... aHandler) {}
    public static void secureServe (String [] aArgs, Node... aHandler) {}
    public static void secureServe (int aPort, Node... aHandler) {}
    public static void secureServe (int aPort, String aHost, Node aHandler) {}
}
