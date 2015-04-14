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
import static javax.servlet.http.HttpServletResponse.SC_FOUND;

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

    public static Response create (HttpServletResponse response) {
        return new Response (response);
    }

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
    public void status (int statusCode) {
        response.setStatus (statusCode);
    }

    /**
     * Sets the content type for the response
     *
     * @param contentType the content type
     */
    public void type (String contentType) {
        response.setContentType (contentType);
    }

    /**
     * Sets the body
     *
     * @param body the body
     */
    public void body (String body) {
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

    public void addDateHeader (String name, long value) {
        response.addDateHeader (name, value);
    }

    /**
     * Trigger a browser redirect
     *
     * @param location Where to redirect
     */
    public void redirect (String location) {
        LOG.fine (format ("Redirecting (%s %s to %s)", "Found", SC_FOUND, location));

        try {
            response.sendRedirect (location);
        }
        catch (IOException ioException) {
            LOG.warning ("Redirect failure: " + ioException.getMessage ());
        }
    }

    /**
     * Trigger a browser redirect with specific http 3XX status code.
     *
     * @param location Where to redirect permanently
     * @param httpStatusCode the http status code
     */
    public void redirect (String location, int httpStatusCode) {
        if (LOG.isLoggable (FINE))
            LOG.fine (format ("Redirecting (%s to %s)", httpStatusCode, location));

        response.setStatus (httpStatusCode);
        response.setHeader ("Location", location);
        response.setHeader ("Connection", "close");
        try {
            response.sendError (httpStatusCode);
        }
        catch (IOException e) {
            LOG.warning ("Exception when trying to redirect permanently: " + e.getMessage ());
        }
    }

    /**
     * Adds/Sets a response header
     *
     * @param header the header
     * @param value the value
     */
    public void header (String header, String value) {
        response.addHeader (header, value);
    }

    /**
     * Adds not persistent cookie to the response.
     * Can be invoked multiple times to insert more than one cookie.
     *
     * @param name name of the cookie
     * @param value value of the cookie
     */
    public void cookie (String name, String value) {
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
    public void cookie (String name, String value, int maxAge) {
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
    public void cookie (String name, String value, int maxAge, boolean secured) {
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
    public void cookie (String path, String name, String value, int maxAge, boolean secured) {
        Cookie cookie = new Cookie (name, value);
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
    public void removeCookie (String name) {
        Cookie cookie = new Cookie (name, "");
        cookie.setMaxAge (0);
        response.addCookie (cookie);
    }
}
