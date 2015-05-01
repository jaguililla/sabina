/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
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

import java.util.function.BiConsumer;

import sabina.Route.Handler;
import sabina.Route.VoidHandler;
import sabina.route.RouteMatcher;

/**
 * Static methods to handle the default server (singleton server)
 */
public final class Sabina {
    private static Server s = new Server ();

    /*
     * Filters
     */
    public static Server after (VoidHandler h) { return s.after (h); }
    public static Server before (VoidHandler h) { return s.before (h); }
    public static Server after (String p, VoidHandler h) { return s.after (p, h); }
    public static Server before (String p, VoidHandler h) { return s.before (p, h); }
    public static Server after (String p, String ct, VoidHandler h) { return s.after (p, ct, h); }
    public static Server before (String p, String ct, VoidHandler h) { return s.before (p, ct, h); }

    /*
     * Routes
     */
    public static Server delete (String p, Handler h) { return s.delete (p, h); }
    public static Server delete (String p, VoidHandler h) { return s.delete (p, h); }
    public static Server get (String p, Handler h) { return s.get (p, h); }
    public static Server get (String p, VoidHandler h) { return s.get (p, h); }
    public static Server head (String p, Handler h) { return s.head (p, h); }
    public static Server head (String p, VoidHandler h) { return s.head (p, h); }
    public static Server options (String p, Handler h) { return s.options (p, h); }
    public static Server options (String p, VoidHandler h) { return s.options (p, h); }
    public static Server patch (String p, Handler h) { return s.patch (p, h); }
    public static Server patch (String p, VoidHandler h) { return s.patch (p, h); }
    public static Server post (String p, Handler h) { return s.post (p, h); }
    public static Server post (String p, VoidHandler h) { return s.post (p, h); }
    public static Server put (String p, Handler h) { return s.put (p, h); }
    public static Server put (String p, VoidHandler h) { return s.put (p, h); }
    public static Server trace (String p, Handler h) { return s.trace (p, h); }
    public static Server trace (String p, VoidHandler h) { return s.trace (p, h); }
    public static Server delete (String p, String ct, Handler h) { return s.delete (p, ct, h); }
    public static Server delete (String p, String ct, VoidHandler h) { return s.delete (p, ct, h); }
    public static Server get (String p, String ct, Handler h) { return s.get (p, ct, h); }
    public static Server get (String p, String ct, VoidHandler h) { return s.get (p, ct, h); }
    public static Server head (String p, String ct, Handler h) { return s.head (p, ct, h); }
    public static Server head (String p, String ct, VoidHandler h) { return s.head (p, ct, h); }
    public static Server options (String p, String ct, Handler h) { return s.options (p, ct, h); }
    public static Server options (String p, String ct, VoidHandler h) { return s.options (p, ct, h); }
    public static Server patch (String p, String ct, Handler h) { return s.patch (p, ct, h); }
    public static Server patch (String p, String ct, VoidHandler h) { return s.patch (p, ct, h); }
    public static Server post (String p, String ct, Handler h) { return s.post (p, ct, h); }
    public static Server post (String p, String ct, VoidHandler h) { return s.post (p, ct, h); }
    public static Server put (String p, String ct, Handler h) { return s.put (p, ct, h); }
    public static Server put (String p, String ct, VoidHandler h) { return s.put (p, ct, h); }
    public static Server trace (String p, String ct, Handler h) { return s.trace (p, ct, h); }
    public static Server trace (String p, String ct, VoidHandler h) { return s.trace (p, ct, h); }

    public static RouteMatcher routeMatcher () {
        return s.routeMatcher;
    }

    public static void host (String host) {
        s.host (host);
    }

    public static void port (int port) {
        s.port (port);
    }

    public static void port (String port) {
        s.port (port);
    }

    public static void secure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        s.secure (keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    public static void secure (String keystoreFile, String keystorePassword) {
        s.secure (keystoreFile, keystorePassword);
    }

    public static void resourcesLocation (String folder) {
        s.resourcesLocation (folder);
    }

    public static void filesLocation (String externalFolder) {
        s.filesLocation (externalFolder);
    }

    public static void filesLocation (String folder, String externalFolder) {
        s.filesLocation (folder, externalFolder);
    }

    public static void start (int port) {
        s.port (port);
        s.start ();
    }

    public static void start () {
        s.start ();
    }

    public static void stop () {
        s.stop ();
    }

    public static <T extends Exception> Server exception(
        Class<T> exceptionClass, BiConsumer<T, Request> handler) {

        return s.exception (exceptionClass, handler);
    }

    public static void reset () {
        s.reset ();
    }

    /**
     * Shortcut for one h server (proxies, etc.)
     *
     * @param h .
     */
    public static void serve (Handler h) {
        // TODO Change 'get' for 'any' (first create the method ;)
        serve ("/", h);
    }

    /**
     * Shortcut for one h server (proxies, etc.)
     *
     * @param p .
     * @param h .
     */
    public static void serve (String p, Handler h) {
        // TODO Change 'get' for 'any' (first create the method ;)
        get (p, h).start ();
    }

    private Sabina () {
        throw new IllegalStateException ();
    }
}
