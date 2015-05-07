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

import sabina.Router.BiHandler;
import sabina.Router.BiVoidHandler;
import sabina.route.RouteMatcher;
import sabina.Router.Handler;
import sabina.Router.VoidHandler;

/**
 * Static methods to handle the default server (singleton server)
 */
public final class Sabina {
    private static Server s = new Server ();

    /*
     * Filters
     */
    public static void after (VoidHandler h) { s.after (h); }
    public static void before (VoidHandler h) { s.before (h); }
    public static void after (String p, VoidHandler h) { s.after (p, h); }
    public static void before (String p, VoidHandler h) { s.before (p, h); }
    public static void after (String p, String ct, VoidHandler h) { s.after (p, ct, h); }
    public static void before (String p, String ct, VoidHandler h) { s.before (p, ct, h); }

    public static void after (BiVoidHandler h) { s.after (h); }
    public static void before (BiVoidHandler h) { s.before (h); }
    public static void after (String p, BiVoidHandler h) { s.after (p, h); }
    public static void before (String p, BiVoidHandler h) { s.before (p, h); }
    public static void after (String p, String ct, BiVoidHandler h) { s.after (p, ct, h); }
    public static void before (String p, String ct, BiVoidHandler h) { s.before (p, ct, h); }

    /*
     * Routes
     */
    public static void delete (String p, Handler h) { s.delete (p, h); }
    public static void delete (String p, VoidHandler h) { s.delete (p, h); }
    public static void get (String p, Handler h) { s.get (p, h); }
    public static void get (String p, VoidHandler h) { s.get (p, h); }
    public static void head (String p, Handler h) { s.head (p, h); }
    public static void head (String p, VoidHandler h) { s.head (p, h); }
    public static void options (String p, Handler h) { s.options (p, h); }
    public static void options (String p, VoidHandler h) { s.options (p, h); }
    public static void patch (String p, Handler h) { s.patch (p, h); }
    public static void patch (String p, VoidHandler h) { s.patch (p, h); }
    public static void post (String p, Handler h) { s.post (p, h); }
    public static void post (String p, VoidHandler h) { s.post (p, h); }
    public static void put (String p, Handler h) { s.put (p, h); }
    public static void put (String p, VoidHandler h) { s.put (p, h); }
    public static void trace (String p, Handler h) { s.trace (p, h); }
    public static void trace (String p, VoidHandler h) { s.trace (p, h); }
    public static void delete (String p, String ct, Handler h) { s.delete (p, ct, h); }
    public static void delete (String p, String ct, VoidHandler h) { s.delete (p, ct, h); }
    public static void get (String p, String ct, Handler h) { s.get (p, ct, h); }
    public static void get (String p, String ct, VoidHandler h) { s.get (p, ct, h); }
    public static void head (String p, String ct, Handler h) { s.head (p, ct, h); }
    public static void head (String p, String ct, VoidHandler h) { s.head (p, ct, h); }
    public static void options (String p, String ct, Handler h) { s.options (p, ct, h); }
    public static void options (String p, String ct, VoidHandler h) { s.options (p, ct, h); }
    public static void patch (String p, String ct, Handler h) { s.patch (p, ct, h); }
    public static void patch (String p, String ct, VoidHandler h) { s.patch (p, ct, h); }
    public static void post (String p, String ct, Handler h) { s.post (p, ct, h); }
    public static void post (String p, String ct, VoidHandler h) { s.post (p, ct, h); }
    public static void put (String p, String ct, Handler h) { s.put (p, ct, h); }
    public static void put (String p, String ct, VoidHandler h) { s.put (p, ct, h); }
    public static void trace (String p, String ct, Handler h) { s.trace (p, ct, h); }
    public static void trace (String p, String ct, VoidHandler h) { s.trace (p, ct, h); }

    public static void delete (String p, BiHandler h) { s.delete (p, h); }
    public static void delete (String p, BiVoidHandler h) { s.delete (p, h); }
    public static void get (String p, BiHandler h) { s.get (p, h); }
    public static void get (String p, BiVoidHandler h) { s.get (p, h); }
    public static void head (String p, BiHandler h) { s.head (p, h); }
    public static void head (String p, BiVoidHandler h) { s.head (p, h); }
    public static void options (String p, BiHandler h) { s.options (p, h); }
    public static void options (String p, BiVoidHandler h) { s.options (p, h); }
    public static void patch (String p, BiHandler h) { s.patch (p, h); }
    public static void patch (String p, BiVoidHandler h) { s.patch (p, h); }
    public static void post (String p, BiHandler h) { s.post (p, h); }
    public static void post (String p, BiVoidHandler h) { s.post (p, h); }
    public static void put (String p, BiHandler h) { s.put (p, h); }
    public static void put (String p, BiVoidHandler h) { s.put (p, h); }
    public static void trace (String p, BiHandler h) { s.trace (p, h); }
    public static void trace (String p, BiVoidHandler h) { s.trace (p, h); }
    public static void delete (String p, String ct, BiHandler h) { s.delete (p, ct, h); }
    public static void delete (String p, String ct, BiVoidHandler h) { s.delete (p, ct, h); }
    public static void get (String p, String ct, BiHandler h) { s.get (p, ct, h); }
    public static void get (String p, String ct, BiVoidHandler h) { s.get (p, ct, h); }
    public static void head (String p, String ct, BiHandler h) { s.head (p, ct, h); }
    public static void head (String p, String ct, BiVoidHandler h) { s.head (p, ct, h); }
    public static void options (String p, String ct, BiHandler h) { s.options (p, ct, h); }
    public static void options (String p, String ct, BiVoidHandler h) { s.options (p, ct, h); }
    public static void patch (String p, String ct, BiHandler h) { s.patch (p, ct, h); }
    public static void patch (String p, String ct, BiVoidHandler h) { s.patch (p, ct, h); }
    public static void post (String p, String ct, BiHandler h) { s.post (p, ct, h); }
    public static void post (String p, String ct, BiVoidHandler h) { s.post (p, ct, h); }
    public static void put (String p, String ct, BiHandler h) { s.put (p, ct, h); }
    public static void put (String p, String ct, BiVoidHandler h) { s.put (p, ct, h); }
    public static void trace (String p, String ct, BiHandler h) { s.trace (p, ct, h); }
    public static void trace (String p, String ct, BiVoidHandler h) { s.trace (p, ct, h); }

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

    public static <T extends Exception> void exception (
        Class<T> exceptionClass, BiConsumer<T, Request> handler) {

        s.exception (exceptionClass, handler);
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
        get (p, h);
        start ();
    }

    private Sabina () {
        throw new IllegalStateException ();
    }
}
