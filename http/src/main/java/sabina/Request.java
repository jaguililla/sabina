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

import static java.util.Collections.unmodifiableMap;
import static java.util.logging.Logger.getLogger;

import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.io.CharStreams;
import sabina.route.RouteMatch;

/**
 * Provides information about the HTTP request
 *
 * @author Per Wendel
 */
public class Request {
    private static final Logger LOG = getLogger(Request.class.getName ());
    private static final String USER_AGENT = "user-agent";

    private static boolean isParam (String routePart) {
        return routePart.startsWith (":");
    }

    private static boolean isSplat (String routePart) {
        return routePart.equals ("*");
    }

    public static Request create (
        RouteMatch match, HttpServletRequest request, HttpServletResponse response) {
        return new Request (match, request, response);
    }

    public static List<String> convertRouteToList (String route) {
        String[] pathArray = route.split ("/");
        List<String> path = new ArrayList<> ();
        for (String p : pathArray)
            if (p.length () > 0)
                path.add (p);

        return path;
    }

    public final Response response;

    private Map<String, String> params;
    private List<String> splat;

    private HttpServletRequest servletRequest;

    private Session session = null;

    /* Lazy loaded stuff */
    private String body = null;

    private Set<String> headers = null;

    //    request.body              # request body sent by the client (see below), DONE
    //    request.scheme            # "http"                                DONE
    //    request.path_info         # "/foo",                               DONE
    //    request.port              # 80                                    DONE
    //    request.request_method    # "GET",                                DONE
    //    request.query_string      # "",                                   DONE
    //    request.content_length    # length of request.body,               DONE
    //    request.media_type        # media type of request.body            DONE, content type?
    //    request.host              # "example.com"                         DONE
    //    request["SOME_HEADER"]    # value of SOME_HEADER header,          DONE
    //    request.user_agent        # user agent (used by :agent condition) DONE
    //    request.url               # "http://example.com/example/foo"      DONE
    //    request.ip                # client IP address                     DONE
    //    request.env               # raw env hash handed in by Rack,       DONE
    //    request.get?              # true (similar methods for other verbs)
    //    request.secure?           # false (would be true over ssl)
    //    request.forwarded?        # true (if running behind a reverse proxy)
    //    request.cookies           # hash of browser cookies,              DONE
    //    request.xhr?              # is this an ajax request?
    //    request.script_name       # "/example"
    //    request.form_data?        # false
    //    request.referrer          # the referrer of the client or '/'

    /**
     * Implemented only for RequestWrapper.
     */
    protected Request () {
        response = null;
    }

    /**
     * Used by wrapper.
     * @param response
     */
    protected Request (HttpServletResponse response) { super ();
        this.response = new Response (response);
    }

    /**
     * Constructor
     *
     * @param match   the route match
     * @param request the servlet request
     */
    Request (RouteMatch match, HttpServletRequest request, HttpServletResponse response) {
        this.servletRequest = request;
        this.response = new Response (response);

        List<String> requestList = convertRouteToList (match.getRequestURI ());
        List<String> matchedList = convertRouteToList (match.getMatchUri ());

        params = getParams(requestList, matchedList);
        splat = getSplat(requestList, matchedList);
    }

    /**
     * Returns the map containing all route params
     *
     * @return a map containing all route params
     */
    public Map<String, String> params () {
        return unmodifiableMap (params);
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     *
     * @param param The param.
     * @return Null if the given param is null or not found.
     */
    public String params (String param) {
        if (param == null)
            return null;

        return param.startsWith (":")?
            params.get (param.toLowerCase ()) :
            params.get (":" + param.toLowerCase ());
    }

    /**
     * @return an array containing the splat (wildcard) parameters
     */
    public String[] splat () {
        return splat.toArray (new String[splat.size ()]);
    }

    /**
     * @return request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod () {
        return servletRequest.getMethod ();
    }

    /**
     * @return the scheme
     */
    public String scheme () {
        return servletRequest.getScheme ();
    }

    /**
     * @return the host
     */
    public String host () {
        return servletRequest.getHeader ("host");
    }

    /**
     * @return the user-agent
     */
    public String userAgent () {
        return servletRequest.getHeader (USER_AGENT);
    }

    /**
     * @return the server port
     */
    public int port () {
        return servletRequest.getServerPort ();
    }

    /**
     * @return the path info
     * Example return: "/example/foo"
     */
    public String pathInfo () {
        return servletRequest.getPathInfo ();
    }

    /**
     * @return the servlet path
     */
    public String servletPath () {
        return servletRequest.getServletPath ();
    }

    /**
     * @return the context path
     */
    public String contextPath () {
        return servletRequest.getContextPath ();
    }

    /**
     * @return the URL string
     */
    public String url () {
        return servletRequest.getRequestURL ().toString ();
    }

    /**
     * @return the content type of the body
     */
    public String contentType () {
        return servletRequest.getContentType ();
    }

    /**
     * @return the client's IP address
     */
    public String ip () {
        return servletRequest.getRemoteAddr ();
    }

    /**
     * @return the request body sent by the client
     */
    public String body () {
        if (body == null) {
            try (InputStreamReader input =
                new InputStreamReader (servletRequest.getInputStream ())) {

                body = CharStreams.toString (input);
            }
            catch (Exception e) {
                LOG.warning ("Exception when reading body: " + e.getMessage ());
            }
        }
        return body;
    }

    /**
     * @return the length of request.body
     */
    public int contentLength () {
        return servletRequest.getContentLength ();
    }

    /**
     * gets the query param
     *
     * @param queryParam the query parameter
     * @return the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String queryParams (String queryParam) {
        return servletRequest.getParameter (queryParam);
    }

    /**
     * Gets the value for the provided header
     *
     * @param name the header
     * @return the value of the provided header
     */
    public String headers (String name) {
        return servletRequest.getHeader (name);
    }

    /**
     * @return all query parameters
     */
    public Set<String> queryParams () {
        return servletRequest.getParameterMap ().keySet ();
    }

    /**
     * @return all headers
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
     * @return the query string
     */
    public String queryString () {
        return servletRequest.getQueryString ();
    }

    /**
     * Sets an attribute on the request (can be fetched in filters/routes later in the chain)
     *
     * @param attribute The attribute
     * @param value     The attribute value
     */
    public void attribute (String attribute, Object value) {
        servletRequest.setAttribute (attribute, value);
    }

    /**
     * Gets the value of the provided attribute
     *
     * @param name The attribute name.
     * @return the value for the provided attribute
     */
    public Object attribute (String name) {
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
     * @return the raw HttpServletRequest object handed in by Jetty
     */
    public HttpServletRequest raw () {
        return servletRequest;
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
    public Session session (boolean create) {
        if (session == null) {
            HttpSession httpSession = servletRequest.getSession (create);
            if (httpSession != null)
                session = new Session (httpSession);
        }
        return session;
    }

    /**
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

    /**
     * Gets cookie by name.
     *
     * @param name name of the cookie
     *
     * @return cookie value or null if the cookie was not found
     */
    public String cookie (String name) {
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
        return servletRequest.getProtocol();
    }

    private static Map<String, String> getParams (List<String> request, List<String> matched) {
        LOG.fine ("get params");

        Map<String, String> params = new HashMap<> ();

        for (int i = 0; (i < request.size ()) && (i < matched.size ()); i++) {
            String matchedPart = matched.get (i);
            if (isParam (matchedPart)) {
                LOG.fine ("matchedPart: "
                    + matchedPart
                    + " = "
                    + request.get (i));
                params.put (matchedPart.toLowerCase (), request.get (i));
            }
        }
        return unmodifiableMap (params);
    }

    private static List<String> getSplat(List<String> request, List<String> matched) {
        LOG.fine("get splat");

        int nbrOfRequestParts = request.size();
        int nbrOfMatchedParts = matched.size();

        boolean sameLength = (nbrOfRequestParts == nbrOfMatchedParts);

        List<String> splat = new ArrayList<> ();

        for (int i = 0; (i < nbrOfRequestParts) && (i < nbrOfMatchedParts); i++) {
            String matchedPart = matched.get(i);

            if (isSplat(matchedPart)) {

                StringBuilder splatParam = new StringBuilder(request.get(i));
                if (!sameLength && (i == (nbrOfMatchedParts - 1))) {
                    for (int j = i + 1; j < nbrOfRequestParts; j++) {
                        splatParam.append ("/");
                        splatParam.append (request.get (j));
                    }
                }
                splat.add (splatParam.toString ());
            }
        }
        return Collections.unmodifiableList (splat);
    }

    /*
     * Response delegates
     */
    public void body (final String body) { response.body (body); }
    public void redirect (final String location) { response.redirect (location); }
    public void type (final String contentType) { response.type (contentType); }
    public HttpServletResponse responseRaw () { return response.raw (); }
    public String responseBody () { return response.body (); }
    public void status (final int statusCode) { response.status (statusCode); }
    public void removeCookie (final String name) { response.removeCookie (name); }
    public void header (final String name, final String value) { response.header (name, value); }
    public void cookie (final String name, final String value) { response.cookie (name, value); }

    public void cookie (final String name, final String value, final int maxAge) {
        response.cookie (name, value, maxAge);
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
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     */
    public void halt () { throw new HaltException (); }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param status the status code.
     */
    public void halt (final int status) { throw new HaltException (status); }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched,
     * re-throw otherwise halt will not work.
     *
     * @param body The body content.
     */
    public void halt (final String body) { throw new HaltException (body); }

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

    /*
     * TODO Implement these methods!
     */
    public void pass () {}
    public void redirect () {}
    public void template (final String template, final Object params) {}
    public void template (final String template, final String layout, final Object params) {}
}
