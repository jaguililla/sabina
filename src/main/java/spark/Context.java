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

package spark;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class Context {
    public final Request request;
    public final Response response;

    protected Context (Request aRequest, Response aResponse) {
        request = aRequest;
        response = aResponse;
    }

    public Map<String, String> params () {
        return request.params ();
    }

    public String cookie (String name) {
        return request.cookie (name);
    }

    public Session session () {
        return request.session ();
    }

    public int port () {
        return request.port ();
    }

    public Object attribute (String attribute) {
        return request.attribute (attribute);
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

    public void attribute (String attribute, Object value) {
        request.attribute (attribute, value);
    }

    public Session session (boolean create) {
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

    public String params (String param) {
        return request.params (param);
    }

    public String host () {
        return request.host ();
    }

    public String scheme () {
        return request.scheme ();
    }

    public String headers (String header) {
        return request.headers (header);
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

    public String queryParams (String queryParam) {
        return request.queryParams (queryParam);
    }

    public QueryParams queryMap (String key) {
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

    public void status (int statusCode) {
        response.status (statusCode);
    }

    public void cookie (String name, String value, int maxAge) {
        response.cookie (name, value, maxAge);
    }

    public void removeCookie (String name) {
        response.removeCookie (name);
    }

    public void header (String header, String value) {
        response.header (header, value);
    }

    public void body (String body) {
        response.body (body);
    }

    public void cookie (String path, String name, String value, int maxAge, boolean secured) {
        response.cookie (path, name, value, maxAge, secured);
    }

    public void redirect (String location, int httpStatusCode) {
        response.redirect (location, httpStatusCode);
    }

    public void cookie (String name, String value) {
        response.cookie (name, value);
    }

    public void cookie (String name, String value, int maxAge, boolean secured) {
        response.cookie (name, value, maxAge, secured);
    }

    public void redirect (String location) {
        response.redirect (location);
    }

    public void type (String contentType) {
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
    public final void halt () {
        throw new HaltException ();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param status the status code.
     */
    public final void halt (int status) {
        throw new HaltException (status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param body The body content.
     */
    public final void halt (String body) {
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
    public final void halt (int status, String body) {
        throw new HaltException (status, body);
    }

    // TODO Implement these methods!

    public void pass () {}

    public void redirect () {}

    public void template (String aTemplate, Object aParams) {}

    public void template (String aTemplate, String aLayout, Object aParams) {}
}
