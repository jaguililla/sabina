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

package sabina;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public final class Context {
    public final Request request;
    public final Response response;

    Context (final Request req, final Response res) {
        if (req == null || res == null)
            throw new IllegalArgumentException ();

        this.request = req;
        this.response = res;
    }

    public Map<String, String> params () {
        return request.params ();
    }

    public String cookie (final String name) {
        return request.cookie (name);
    }

    public Session session () {
        return request.session ();
    }

    public int port () {
        return request.port ();
    }

    public Object attribute (final String name) {
        return request.attribute (name);
    }

    public String queryString () {
        return request.queryString ();
    }

    public String userAgent () {
        return request.userAgent ();
    }

    public QueryParams queryMap () {
        return request.queryMap ();
    }

    public Set<String> queryParams () {
        return request.queryParams ();
    }

    public Set<String> attributes () {
        return request.attributes ();
    }

    public void attribute (final String name, final Object value) {
        request.attribute (name, value);
    }

    public Session session (final boolean create) {
        return request.session (create);
    }

    public String pathInfo () {
        return request.pathInfo ();
    }

    public String contextPath () {
        return request.contextPath ();
    }

    public String servletPath () {
        return request.servletPath ();
    }

    public String params (final String name) {
        return request.params (name);
    }

    public String host () {
        return request.host ();
    }

    public String scheme () {
        return request.scheme ();
    }

    public String headers (final String name) {
        return request.headers (name);
    }

    public String contentType () {
        return request.contentType ();
    }

    public Set<String> headers () {
        return request.headers ();
    }

    public Map<String, String> cookies () {
        return request.cookies ();
    }

    public int contentLength () {
        return request.contentLength ();
    }

    public String url () {
        return request.url ();
    }

    public String[] splat () {
        return request.splat ();
    }

    public String queryParams (final String name) {
        return request.queryParams (name);
    }

    public QueryParams queryMap (final String key) {
        return request.queryMap (key);
    }

    public String ip () {
        return request.ip ();
    }

    public HttpServletRequest requestRaw () {
        return request.raw ();
    }

    public String requestBody () {
        return request.body ();
    }

    public String requestMethod () {
        return request.requestMethod ();
    }

    public void status (final int statusCode) {
        response.status (statusCode);
    }

    public void cookie (final String name, final String value, final int maxAge) {
        response.cookie (name, value, maxAge);
    }

    public void removeCookie (final String name) {
        response.removeCookie (name);
    }

    public void header (final String name, final String value) {
        response.header (name, value);
    }

    public void body (final String body) {
        response.body (body);
    }

    public void cookie (
        final String path,
        final String name,
        final String value,
        final int maxAge,
        final boolean secured) {

        response.cookie (path, name, value, maxAge, secured);
    }

    public void redirect (final String location, final int httpStatusCode) {
        response.redirect (location, httpStatusCode);
    }

    public void cookie (final String name, final String value) {
        response.cookie (name, value);
    }

    public void cookie (
        final String name, final String value, final int maxAge, final boolean secured) {

        response.cookie (name, value, maxAge, secured);
    }

    public void redirect (final String location) {
        response.redirect (location);
    }

    public void type (final String contentType) {
        response.type (contentType);
    }

    public HttpServletRequest responseRaw () {
        return request.raw ();
    }

    public String responseBody () {
        return request.body ();
    }

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     */
    public void halt () {
        throw new HaltException ();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param status the status code.
     */
    public void halt (final int status) {
        throw new HaltException (status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param body The body content.
     */
    public void halt (final String body) {
        throw new HaltException (body);
    }

    /**
     * Immediately stops a request within a filter or route with specified status code and body
     * content.
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param status The status code.
     * @param body The body content.
     */
    public void halt (final int status, final String body) {
        throw new HaltException (status, body);
    }

    // TODO Implement these methods!

    public void pass () {}

    public void redirect () {}

    public void template (final String template, final Object params) {}

    public void template (final String template, final String layout, final Object params) {}
}
