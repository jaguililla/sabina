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

import static java.lang.Math.min;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.logging.Logger.getLogger;
import static sabina.util.Builders.entry;

import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sabina.route.RouteMatch;

/**
 * Provides information about the HTTP request.
 *
 * servletRequest.getPathTranslated (); // Script name * request.script_name       # "/example"
 * servletRequest.getLocalName ();
 * servletRequest.getLocalPort ();
 * servletRequest.getLocalAddr ();
 * servletRequest.getRemoteHost ();
 * servletRequest.getRemotePort ();
 *
 * @author Per Wendel
 */
public final class Request {
    private static final Logger LOG = getLogger(Request.class.getName ());
    private static final String USER_AGENT = "user-agent";

    public static List<String> convertRouteToList (final String route) {
        String[] pathArray = route.split ("/");
        List<String> path = new ArrayList<> ();
        for (String p : pathArray)
            if (p.length () > 0)
                path.add (p);

        return path;
    }

    public final Response response;
    private final Map<String, String> params;
    private final HttpServletRequest servletRequest;

    /* Lazy loaded stuff */
    private Session session;
    private String body;
    private Set<String> headers;

    /**
     * Constructor.
     *
     * @param match The route match.
     * @param request The servlet request.
     * @param response The servlet response.
     */
    public Request (
        final RouteMatch match,
        final HttpServletRequest request,
        final HttpServletResponse response) {

        this.servletRequest = request;
        this.response = new Response (response);

        List<String> requestList = convertRouteToList (match.requestURI);
        List<String> matchedList = match.entry.routeParts;

        final Entry<Map<String, String>, List<String>> params = getParams (requestList, matchedList);
        this.params = params.getKey ();
    }

    private Entry<Map<String, String>, List<String>> getParams (
        final List<String> request, final List<String> matched) {

        Map<String, String> params = new HashMap<> ();
        List<String> splat = new ArrayList<> ();

        int smaller = min (request.size (), matched.size ());
        boolean sameLength = (request.size () == matched.size ());

        for (int ii = 0; ii < smaller; ii++) {
            String matchedPart = matched.get (ii);

            if (matchedPart.startsWith (":")) {
                LOG.fine ("matchedPart: " + matchedPart + " = " + request.get (ii));
                params.put (matchedPart.toLowerCase (), request.get (ii));
            }
            else if (matchedPart.equals ("*")) {
                StringBuilder splatParam = new StringBuilder(request.get(ii));
                if (!sameLength && (ii == (matched.size () - 1))) {
                    for (int j = ii + 1; j < request.size (); j++) {
                        splatParam.append ("/");
                        splatParam.append (request.get (j));
                    }
                }
                splat.add (splatParam.toString ());
            }
        }

        return entry (unmodifiableMap (params), unmodifiableList (splat));
    }

    /**
     * request.url      # "http://example.com/example/foo"
     *
     * @return
     */
    public StringBuffer requestUrl () {
        return servletRequest.getRequestURL ();
    }

    /**
     * request.forwarded?        # true (if running behind a reverse proxy)
     *
     * @return
     */
    public boolean forwarded () {
        Object originalRequestURI =
            servletRequest.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);

        return originalRequestURI != null;
    }

    /**
     * Returns the map containing all route params.
     *
     * @return A map containing all route params.
     */
    public Map<String, String> params () {
        return params;
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name').
     *
     * @param param The param.
     * @return Null if the given param is null or not found.
     */
    public String params (final String param) {
        if (param == null)
            return null;

        return param.startsWith (":")?
            params.get (param.toLowerCase ()) :
            params.get (":" + param.toLowerCase ());
    }

    /**
     * @return request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod () {
        return servletRequest.getMethod ();
    }

    /**
     * request.get?              # true (similar methods for other verbs)
     *
     * @return
     */
    public boolean isGet () {
        return requestMethod ().equals ("GET");
    }

    public boolean isPost () {
        return requestMethod ().equals ("Post");
    }

    public boolean isPut () {
        return requestMethod ().equals ("Put");
    }

    public boolean isDelete () {
        return requestMethod ().equals ("Delete");
    }

    /**
     * @return the scheme (http/https).
     */
    public String scheme () {
        return servletRequest.getScheme ();
    }

    /**
     * @return the host (ie: example.com).
     */
    public String host () {
        return servletRequest.getServerName ();
    }

    /**
     * request.user_agent        # user agent (used by :agent condition)
     * @return the user-agent.
     */
    public String userAgent () {
        return servletRequest.getHeader (USER_AGENT);
    }

    /**
     * @return the server port.
     */
    public int port () {
        return servletRequest.getServerPort ();
    }

    /**
     * Example return: "/example/foo".
     *
     * @return the path info.
     */
    public String pathInfo () {
        return servletRequest.getPathInfo ();
    }

    /**
     * @return the servlet path.
     */
    public String servletPath () {
        return servletRequest.getServletPath ();
    }

    /**
     * @return the context path.
     */
    public String contextPath () {
        return servletRequest.getContextPath ();
    }

    /**
     * @return the URL string.
     */
    public String url () {
        return servletRequest.getRequestURL ().toString ();
    }

    /**
     * request.media_type        # media type of request.body
     * @return the content type of the body.
     */
    public String contentType () {
        return servletRequest.getContentType ();
    }

    /**
     * request.ip                # client IP address
     * @return the client's IP address.
     */
    public String ip () {
        return servletRequest.getRemoteAddr ();
    }

    /**
     * @return the request body sent by the client.
     */
    public String body () {
        if (body == null) {
            try (InputStreamReader in = new InputStreamReader (servletRequest.getInputStream ())) {
                body = new Scanner (in).useDelimiter ("\\A").next ();
            }
            catch (Exception e) {
                throw new RuntimeException ("Exception when reading body", e);
            }
        }
        return body;
    }

    /**
     * @return the length of request.body.
     */
    public int contentLength () {
        return servletRequest.getContentLength ();
    }

    /**
     * Gets the query param.
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     *
     * @param queryParam the query parameter.
     * @return the value of the provided queryParam.
     */
    public String queryParams (final String queryParam) {
        return servletRequest.getParameter (queryParam);
    }

    /**
     * Gets the value for the provided header.
     * request["SOME_HEADER"]    # value of SOME_HEADER header
     *
     * @param name the header.
     * @return the value of the provided header.
     */
    public String headers (final String name) {
        return servletRequest.getHeader (name);
    }

    /**
     * @return all query parameters.
     */
    public Set<String> queryParams () {
        return servletRequest.getParameterMap ().keySet ();
    }

    /**
     * @return all headers.
     */
    public Set<String> headers () {
        if (headers == null) {
            headers = new TreeSet<> ();
            Enumeration<String> enumeration = servletRequest.getHeaderNames ();
            while (enumeration.hasMoreElements ())
                headers.add (enumeration.nextElement ());
        }
        return headers;
    }

    /**
     * @return the query string.
     */
    public String queryString () {
        return servletRequest.getQueryString ();
    }

    /**
     * Sets an attribute on the request (can be fetched in filters/routes later in the chain).
     *
     * @param attribute The attribute.
     * @param value The attribute value.
     */
    public void attribute (final String attribute, final Object value) {
        servletRequest.setAttribute (attribute, value);
    }

    /**
     * Gets the value of the provided attribute.
     *
     * @param name The attribute name.
     * @return the value for the provided attribute.
     */
    public Object attribute (final String name) {
        return servletRequest.getAttribute (name);
    }

    /**
     * @return all attributes
     */
    public Set<String> attributes () {
        Set<String> attrList = new HashSet<> ();
        Enumeration<String> attributes = servletRequest.getAttributeNames ();
        while (attributes.hasMoreElements ())
            attrList.add (attributes.nextElement ());
        return attrList;
    }

    /**
     * Returns the current session associated with this request,
     * or if the request does not have a session, creates one.
     *
     * @return the session associated with this request
     */
    public Session session () {
        if (session == null)
            session = new Session (servletRequest.getSession ());

        return session;
    }

    /**
     * Returns the current session associated with this request, or if there is
     * no current session and <code>create</code> is true, returns  a new session.
     *
     * @param create <code>true</code> to create a new session for this request if necessary;
     * <code>false</code> to return null if there's no current session
     *
     * @return the session associated with this request or <code>null</code> if
     * <code>create</code> is <code>false</code> and the request has no valid session
     */
    public Session session (final boolean create) {
        if (session == null) {
            HttpSession httpSession = servletRequest.getSession (create);
            if (httpSession != null)
                session = new Session (httpSession);
        }
        return session;
    }

    /**
     * request.cookies           # hash of browser cookies
     * @return request cookies (or empty Map if cookies dosn't present)
     */
    public Map<String, String> cookies () {
        Map<String, String> result = new HashMap<> ();
        Cookie[] cookies = servletRequest.getCookies ();
        if (cookies != null)
            for (Cookie cookie : cookies)
                result.put (cookie.getName (), cookie.getValue ());

        return result;
    }

    public Cookie[] getCookies () {
        return servletRequest.getCookies ();
    }

    /**
     * Gets cookie by name.
     *
     * @param name name of the cookie
     *
     * @return cookie value or null if the cookie was not found
     */
    public String cookie (final String name) {
        Cookie[] cookies = servletRequest.getCookies ();
        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName ().equals (name))
                    return cookie.getValue ();

        return null;
    }

    /**
     * @return the part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.
     */
    public String uri() {
        return servletRequest.getRequestURI();
    }

    /**
     * @return Returns the name and version of the protocol the request uses
     */
    public String protocol() {
        return servletRequest.getProtocol ();
    }

    /*
     * Response delegates
     */
    public void body (final String body) { response.body (body); }
    public void redirect (final String location) { response.redirect (location); }
    public void type (final String contentType) { response.type (contentType); }
//    public HttpServletResponse responseRaw () { return response.raw (); }
    public String responseBody () { return response.body (); }
    public void status (final int statusCode) { response.status (statusCode); }
    public void removeCookie (final String name) { response.removeCookie (name); }
    public void header (final String name, final String value) { response.header (name, value); }
    public void cookie (final String name, final String value) { response.cookie (name, value); }

    public void cookie (final String name, final String value, final int maxAge) {
        response.cookie (name, value, maxAge);
    }

    /**
     * request.referrer          # the referrer of the client or '/'
     * @return
     */
    public String referrer () {
        return servletRequest.getHeader("referer");
    }

    /**
     * request.secure?           # false (would be true over ssl)
     *
     * @return
     */
    public boolean secure () {
        return servletRequest.isSecure ();
    }

    public void cookie (
        final String path,
        final String name,
        final String value,
        final int maxAge,
        final boolean secured) {

        response.cookie (path, name, value, maxAge, secured);
    }

    public void cookie (
        final String name, final String value, final int maxAge, final boolean secured) {

        response.cookie (name, value, maxAge, secured);
    }

    public void redirect (final String location, final int httpStatusCode) {
        response.redirect (location, httpStatusCode);
    }

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type EndException, or if catched,
     * re-throw otherwise halt will not work.
     */
    public void halt () { throw new EndException (); }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type EndException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param status the status code.
     */
    public void halt (final int status) { throw new EndException (status); }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type EndException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param body The body content.
     */
    public void halt (final String body) { throw new EndException (body); }

    /**
     * Immediately stops a request within a filter or route with specified status code and body
     * content.
     * NOTE: When using this don't catch exceptions of type EndException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param status The status code.
     * @param body The body content.
     */
    public void halt (final int status, final String body) {
        throw new EndException (status, body);
    }

    /*
     * TODO Implement these methods!
     */
    public void pass () {}
    public void template (final String template, final Object params) {}
    public void template (final String template, final String layout, final Object params) {}
}
