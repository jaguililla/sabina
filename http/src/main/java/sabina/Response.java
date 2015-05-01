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
import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides functionality for modifying the response.
 *
 * @author Per Wendel
 */
public final class Response {
    private static final Logger LOG = getLogger (Response.class.getName ());

    private final HttpServletResponse response;
    private String body;

    Response (final HttpServletResponse response) {
        this.response = response;
    }

    /**
     * Sets the status code for the
     *
     * @param statusCode the status code
     */
    public void status (final int statusCode) {
        response.setStatus (statusCode);
    }

    /**
     * Sets the content type for the response
     *
     * @param contentType the content type
     */
    public void type (final String contentType) {
        response.setContentType (contentType);
    }

    /**
     * Sets the body
     *
     * @param body the body
     */
    public void body (final String body) {
        this.body = body;
    }

    /**
     * returns the body
     *
     * @return the body
     */
    public String body () {
        return this.body;
    }

    public void addDateHeader (final String name, final long value) {
        response.addDateHeader (name, value);
    }

    /**
     * Trigger a browser redirect
     *
     * @param location Where to redirect
     */
    public void redirect (final String location) {
        if (LOG.isLoggable (FINE))
            LOG.fine (format ("Redirecting to %s", location));

        try {
            response.sendRedirect (location);
        }
        catch (IOException e) {
            throw new RuntimeException ("Error redirecting to: " + location, e);
        }
    }

    /**
     * Trigger a browser redirect with specific http 3XX status code.
     *
     * @param location Where to redirect permanently
     * @param httpStatusCode the http status code
     */
    public void redirect (final String location, final int httpStatusCode) {
        if (LOG.isLoggable (FINE))
            LOG.fine (format ("Redirecting (%s to %s)", httpStatusCode, location));

        response.setStatus (httpStatusCode);
        response.setHeader ("Location", location);
        response.setHeader ("Connection", "close");
        try {
            response.sendError (httpStatusCode);
        }
        catch (IOException e) {
            throw new RuntimeException ("Exception redirecting permanently to: " + location, e);
        }
    }

    /**
     * Adds/Sets a response header
     *
     * @param header the header
     * @param value the value
     */
    public void header (final String header, final String value) {
        response.addHeader (header, value);
    }

    /**
     * Adds not persistent cookie to the response.
     * Can be invoked multiple times to insert more than one cookie.
     *
     * @param name name of the cookie
     * @param value value of the cookie
     */
    public void cookie (final String name, final String value) {
        cookie (name, value, -1, false);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one
     * cookie.
     *
     * @param name name of the cookie
     * @param value value of the cookie
     * @param maxAge max age of the cookie in seconds (negative for the not persistent cookie,
     * zero - deletes the cookie)
     */
    public void cookie (final String name, final String value, final int maxAge) {
        cookie (name, value, maxAge, false);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one
     * cookie.
     *
     * @param name name of the cookie
     * @param value value of the cookie
     * @param maxAge max age of the cookie in seconds (negative for the not persistent cookie,
     * zero - deletes the cookie)
     * @param secured if true : cookie will be secured
     * zero - deletes the cookie)
     */
    public void cookie (
        final String name, final String value, final int maxAge, final boolean secured) {

        cookie ("", name, value, maxAge, secured);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one
     * cookie.
     *
     * @param path path of the cookie
     * @param name name of the cookie
     * @param value value of the cookie
     * @param maxAge max age of the cookie in seconds (negative for the not persistent cookie,
     * zero - deletes the cookie)
     * @param secured if true : cookie will be secured
     * zero - deletes the cookie)
     */
    public void cookie (
        final String path,
        final String name,
        final String value,
        final int maxAge,
        final boolean secured) {

        final Cookie cookie = new Cookie (name, value);
        cookie.setPath (path);
        cookie.setMaxAge (maxAge);
        cookie.setSecure (secured);

        response.addCookie (cookie);
    }

    /**
     * Removes the cookie.
     *
     * @param name Name of the cookie.
     */
    public void removeCookie (final String name) {
        final Cookie cookie = new Cookie (name, "");
        cookie.setMaxAge (0);

        response.addCookie (cookie);
    }
}
