/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package sabina;

import java.util.function.BiConsumer;

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
    public static Server connect (String path, Route.Handler handler) {
        return server.connect (path, handler);
    }

    public static Server delete (String path, Route.Handler handler) {
        return server.delete (path, handler);
    }

    public static Server get (String path, Route.Handler handler) {
        return server.get (path, handler);
    }

    public static Server head (String path, Route.Handler handler) {
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

    public static Server connect (String path, String contentType, Route.Handler handler) {
        return server.connect (path, contentType, handler);
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

    public static void setIpAddress (String ipAddress) {
        server.setIpAddress (ipAddress);
    }

    public static void setPort (int port) {
        server.setPort (port);
    }

    public static void setSecure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        server.setSecure (keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    public static void staticFileLocation (String folder) {
        server.staticFileLocation (folder);
    }

    public static void externalStaticFileLocation (String externalFolder) {
        server.externalStaticFileLocation (externalFolder);
    }

    public static void start (int port) {
        server.setPort (port);
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
        throw new IllegalArgumentException ();
    }
}
