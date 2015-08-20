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
import sabina.Router.Handler;
import sabina.Router.VoidHandler;

/**
 * Static methods to handle the default server (singleton server)
 */
public final class Sabina {
    public static final Server SERVER = new Server ();

    /*
     * Filters
     */
    public static void after (VoidHandler h) { SERVER.after (h); }
    public static void before (VoidHandler h) { SERVER.before (h); }
    public static void after (String p, VoidHandler h) { SERVER.after (p, h); }
    public static void before (String p, VoidHandler h) { SERVER.before (p, h); }
    public static void after (String p, String ct, VoidHandler h) { SERVER.after (p, ct, h); }
    public static void before (String p, String ct, VoidHandler h) { SERVER.before (p, ct, h); }

    public static void after (BiVoidHandler h) { SERVER.after (h); }
    public static void before (BiVoidHandler h) { SERVER.before (h); }
    public static void after (String p, BiVoidHandler h) { SERVER.after (p, h); }
    public static void before (String p, BiVoidHandler h) { SERVER.before (p, h); }
    public static void after (String p, String ct, BiVoidHandler h) { SERVER.after (p, ct, h); }
    public static void before (String p, String ct, BiVoidHandler h) { SERVER.before (p, ct, h); }

    /*
     * Routes
     */
    public static void delete (String p, Handler h) { SERVER.delete (p, h); }
    public static void delete (String p, VoidHandler h) { SERVER.delete (p, h); }
    public static void get (String p, Handler h) { SERVER.get (p, h); }
    public static void get (String p, VoidHandler h) { SERVER.get (p, h); }
    public static void head (String p, Handler h) { SERVER.head (p, h); }
    public static void head (String p, VoidHandler h) { SERVER.head (p, h); }
    public static void options (String p, Handler h) { SERVER.options (p, h); }
    public static void options (String p, VoidHandler h) { SERVER.options (p, h); }
    public static void patch (String p, Handler h) { SERVER.patch (p, h); }
    public static void patch (String p, VoidHandler h) { SERVER.patch (p, h); }
    public static void post (String p, Handler h) { SERVER.post (p, h); }
    public static void post (String p, VoidHandler h) { SERVER.post (p, h); }
    public static void put (String p, Handler h) { SERVER.put (p, h); }
    public static void put (String p, VoidHandler h) { SERVER.put (p, h); }
    public static void trace (String p, Handler h) { SERVER.trace (p, h); }
    public static void trace (String p, VoidHandler h) { SERVER.trace (p, h); }
    public static void delete (String p, String ct, Handler h) { SERVER.delete (p, ct, h); }
    public static void delete (String p, String ct, VoidHandler h) { SERVER.delete (p, ct, h); }
    public static void get (String p, String ct, Handler h) { SERVER.get (p, ct, h); }
    public static void get (String p, String ct, VoidHandler h) { SERVER.get (p, ct, h); }
    public static void head (String p, String ct, Handler h) { SERVER.head (p, ct, h); }
    public static void head (String p, String ct, VoidHandler h) { SERVER.head (p, ct, h); }
    public static void options (String p, String ct, Handler h) { SERVER.options (p, ct, h); }
    public static void options (String p, String ct, VoidHandler h) { SERVER.options (p, ct, h); }
    public static void patch (String p, String ct, Handler h) { SERVER.patch (p, ct, h); }
    public static void patch (String p, String ct, VoidHandler h) { SERVER.patch (p, ct, h); }
    public static void post (String p, String ct, Handler h) { SERVER.post (p, ct, h); }
    public static void post (String p, String ct, VoidHandler h) { SERVER.post (p, ct, h); }
    public static void put (String p, String ct, Handler h) { SERVER.put (p, ct, h); }
    public static void put (String p, String ct, VoidHandler h) { SERVER.put (p, ct, h); }
    public static void trace (String p, String ct, Handler h) { SERVER.trace (p, ct, h); }
    public static void trace (String p, String ct, VoidHandler h) { SERVER.trace (p, ct, h); }

    public static void delete (String p, BiHandler h) { SERVER.delete (p, h); }
    public static void delete (String p, BiVoidHandler h) { SERVER.delete (p, h); }
    public static void get (String p, BiHandler h) { SERVER.get (p, h); }
    public static void get (String p, BiVoidHandler h) { SERVER.get (p, h); }
    public static void head (String p, BiHandler h) { SERVER.head (p, h); }
    public static void head (String p, BiVoidHandler h) { SERVER.head (p, h); }
    public static void options (String p, BiHandler h) { SERVER.options (p, h); }
    public static void options (String p, BiVoidHandler h) { SERVER.options (p, h); }
    public static void patch (String p, BiHandler h) { SERVER.patch (p, h); }
    public static void patch (String p, BiVoidHandler h) { SERVER.patch (p, h); }
    public static void post (String p, BiHandler h) { SERVER.post (p, h); }
    public static void post (String p, BiVoidHandler h) { SERVER.post (p, h); }
    public static void put (String p, BiHandler h) { SERVER.put (p, h); }
    public static void put (String p, BiVoidHandler h) { SERVER.put (p, h); }
    public static void trace (String p, BiHandler h) { SERVER.trace (p, h); }
    public static void trace (String p, BiVoidHandler h) { SERVER.trace (p, h); }
    public static void delete (String p, String ct, BiHandler h) { SERVER.delete (p, ct, h); }
    public static void delete (String p, String ct, BiVoidHandler h) { SERVER.delete (p, ct, h); }
    public static void get (String p, String ct, BiHandler h) { SERVER.get (p, ct, h); }
    public static void get (String p, String ct, BiVoidHandler h) { SERVER.get (p, ct, h); }
    public static void head (String p, String ct, BiHandler h) { SERVER.head (p, ct, h); }
    public static void head (String p, String ct, BiVoidHandler h) { SERVER.head (p, ct, h); }
    public static void options (String p, String ct, BiHandler h) { SERVER.options (p, ct, h); }
    public static void options (String p, String ct, BiVoidHandler h) { SERVER.options (p, ct, h); }
    public static void patch (String p, String ct, BiHandler h) { SERVER.patch (p, ct, h); }
    public static void patch (String p, String ct, BiVoidHandler h) { SERVER.patch (p, ct, h); }
    public static void post (String p, String ct, BiHandler h) { SERVER.post (p, ct, h); }
    public static void post (String p, String ct, BiVoidHandler h) { SERVER.post (p, ct, h); }
    public static void put (String p, String ct, BiHandler h) { SERVER.put (p, ct, h); }
    public static void put (String p, String ct, BiVoidHandler h) { SERVER.put (p, ct, h); }
    public static void trace (String p, String ct, BiHandler h) { SERVER.trace (p, ct, h); }
    public static void trace (String p, String ct, BiVoidHandler h) { SERVER.trace (p, ct, h); }

    public static <T extends Exception> void exception (
        Class<T> exceptionClass, BiConsumer<T, Request> handler) {

        SERVER.exception (exceptionClass, handler);
    }

    public static String host () {
        return SERVER.bind ();
    }

    public static void host (String host) {
        SERVER.bind (host);
    }

    public static int port () {
        return SERVER.port ();
    }

    public static void port (int port) {
        SERVER.port (port);
    }

    public static void port (String port) {
        SERVER.port (port);
    }

    public static void secure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        SERVER.secure (keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    public static void secure (String keystoreFile, String keystorePassword) {
        SERVER.secure (keystoreFile, keystorePassword);
    }

    public static void resourcesLocation (String folder) {
        SERVER.resourcesLocation (folder);
    }

    public static void filesLocation (String externalFolder) {
        SERVER.filesLocation (externalFolder);
    }

    public static void filesLocation (String folder, String externalFolder) {
        SERVER.filesLocation (folder, externalFolder);
    }

    public static void start (int port) {
        SERVER.port (port);
        SERVER.start ();
    }

    public static void start () {
        SERVER.start ();
    }

    public static void stop () {
        SERVER.stop ();
    }

    public static void reset () {
        SERVER.reset ();
    }

    /**
     * Shortcut for one h server (proxies, etc.)
     *
     * @param h .
     */
    public static void serve (Handler h) {
        serve ("/", h);
    }

    /**
     * Shortcut for one h server (proxies, etc.)
     *
     * @param p .
     * @param h .
     */
    public static void serve (String p, Handler h) {
        // TODO Change 'get' for 'before' allow filters to return a value (setting it to body)
        get (p, h);
        start ();
    }

    private Sabina () {
        throw new IllegalStateException ();
    }
}
