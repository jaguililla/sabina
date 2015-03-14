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
import java.util.function.Consumer;

/**
 * Static methods to handle the default server (singleton server)
 */
public final class Sabina {
    private static Server server = new Server ();

    /*
     * Filters
     */
    public static Server after (Filter.Handler handler) {
        return server.after (handler);
    }

    public static Server before (Filter.Handler handler) {
        return server.before (handler);
    }

    public static Server after (String path, Filter.Handler handler) {
        return server.after (path, handler);
    }

    public static Server before (String path, Filter.Handler handler) {
        return server.before (path, handler);
    }

    public static Server after (String path, String contentType, Filter.Handler handler) {
        return server.after (path, contentType, handler);
    }

    public static Server before (String path, String contentType, Filter.Handler handler) {
        return server.before (path, contentType, handler);
    }

    /*
     * Routes
     */
    public static Server delete (String path, Route.Handler handler) {
        return server.delete (path, handler);
    }

    public static Server get (String path, Route.Handler handler) {
        return server.get (path, handler);
    }

    public static Server head (String path, Route.Handler handler) {
        return server.head (path, handler);
    }

    public static Server head (String path, Consumer<Request> handler) {
        return server.head (path, handler);
    }

    public static Server options (String path, Route.Handler handler) {
        return server.options (path, handler);
    }

    public static Server patch (String path, Route.Handler handler) {
        return server.patch (path, handler);
    }

    public static Server post (String path, Route.Handler handler) {
        return server.post (path, handler);
    }

    public static Server put (String path, Route.Handler handler) {
        return server.put (path, handler);
    }

    public static Server trace (String path, Route.Handler handler) {
        return server.trace (path, handler);
    }

    public static Server delete (String path, String contentType, Route.Handler handler) {
        return server.delete (path, contentType, handler);
    }

    public static Server get (String path, String contentType, Route.Handler handler) {
        return server.get (path, contentType, handler);
    }

    public static Server head (String path, String contentType, Route.Handler handler) {
        return server.head (path, contentType, handler);
    }

    public static Server options (String path, String contentType, Route.Handler handler) {
        return server.options (path, contentType, handler);
    }

    public static Server patch (String path, String contentType, Route.Handler handler) {
        return server.patch (path, contentType, handler);
    }

    public static Server post (String path, String contentType, Route.Handler handler) {
        return server.post (path, contentType, handler);
    }

    public static Server put (String path, String contentType, Route.Handler handler) {
        return server.put (path, contentType, handler);
    }

    public static Server trace (String path, String contentType, Route.Handler handler) {
        return server.trace (path, contentType, handler);
    }

    public static void host (String host) {
        server.host (host);
    }

    public static void port (int port) {
        server.port (port);
    }

    public static void port (String port) {
        server.port (port);
    }

    public static void secure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        server.secure (keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    public static void staticFileLocation (String folder) {
        server.staticFileLocation (folder);
    }

    public static void externalStaticFileLocation (String externalFolder) {
        server.externalStaticFileLocation (externalFolder);
    }

    public static void start (int port) {
        server.port (port);
        server.start ();
    }

    public static void start () {
        server.start ();
    }

    public static void stop () {
        server.stop ();
    }

    public static <T extends Exception> Server exception(
        Class<T> exceptionClass, BiConsumer<T, Request> aHandler) {

        return server.exception (exceptionClass, aHandler);
    }

    private Sabina () {
        throw new IllegalStateException ();
    }
}
