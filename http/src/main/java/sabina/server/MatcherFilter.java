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

package sabina.server;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static sabina.HttpMethod.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sabina.*;
import sabina.route.RouteMatch;
import sabina.route.RouteMatcher;

/**
 * Filter for matching of filters and routes.
 *
 * @author Per Wendel
 */
public class MatcherFilter implements Filter, Router {
    private static final Logger LOG = getLogger (MatcherFilter.class.getName ());

    private static final String
        ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept",
        INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>",
        NOT_FOUND =
            "<html><body>" +
                "<h2>404 Not found</h2>The requested route [%s] has not been mapped in Sabina" +
                "</body></html>";

    public final RouteMatcher routeMatcher;
    public final boolean hasOtherHandlers;
    public final String backend;

    boolean handled;

    /**
     * TODO Needed by Undertow to instantiate the filter.
     */
    public MatcherFilter () {
        routeMatcher = null;
        backend = "undertow";
        hasOtherHandlers = false;
    }

    /**
     * Constructor.
     *
     * @param routeMatcher The route matcher.
     * @param backend TODO .
     * @param hasOtherHandlers If true, do nothing if request is not consumed by Sabina in order
     * to let others handlers process the request.
     */
    public MatcherFilter (
        RouteMatcher routeMatcher, String backend, boolean hasOtherHandlers) {

        this.routeMatcher = routeMatcher;
        this.backend = backend;
        this.hasOtherHandlers = hasOtherHandlers;
    }

    @Override public void doFilter (
        final ServletRequest servletRequest,
        final ServletResponse servletResponse,
        final FilterChain chain) throws IOException, ServletException {

        boolean loggable = LOG.isLoggable (FINE);
        long t = loggable? currentTimeMillis () : 0;

        final HttpServletRequest httpReq = (HttpServletRequest)servletRequest;
        final HttpServletResponse httpRes = (HttpServletResponse)servletResponse;

        final String uri = httpReq.getRequestURI ();
        final String httpMethodStr = httpReq.getMethod ();
        final String acceptType = httpReq.getHeader (ACCEPT_TYPE_REQUEST_MIME_HEADER);

        String bodyContent = null;

        try {
            bodyContent = onFilter (BEFORE, httpReq, httpRes, uri, acceptType, bodyContent);

            final HttpMethod httpMethod = HttpMethod.valueOf (httpMethodStr);
            RouteMatch match = routeMatcher.findTarget (httpMethod, uri, acceptType);

            if (match == null && httpMethod == HEAD && bodyContent == null) {
                // See if get is mapped to provide default head mapping
                RouteMatch requestedRouteTarget = routeMatcher.findTarget (GET, uri, acceptType);
                bodyContent = requestedRouteTarget != null? "" : null;
            }

            if (match != null && match.entry != null) {
                bodyContent = handleTargetRoute (httpReq, httpRes, bodyContent, match, match.entry);
            }

            bodyContent = onFilter (AFTER, httpReq, httpRes, uri, acceptType, bodyContent);
        }
        catch (HaltException e) {
            if (loggable)
                LOG.fine ("halt performed");
            httpRes.setStatus (e.statusCode);
            String haltBody = e.body;
            bodyContent = (haltBody != null)? haltBody : "";
        }

        // If redirected and content is null set to empty string to not throw NotConsumedException
//        if (bodyContent == null && res.isRedirected())
//            bodyContent = "";
        // TODO Check this scenario
        // TODO add header to know if has been redirected (ie: __REDIRECTED__)

        boolean consumed = bodyContent != null;

        if (!consumed && hasOtherHandlers) {
            handled = false;
			if (backend.equals ("undertow"))
				httpRes.setStatus (SC_NOT_FOUND); // TODO Only for Undertow
            return;
        }

        if (!consumed) {
            httpRes.setStatus (SC_NOT_FOUND);
            bodyContent = format (NOT_FOUND, uri);
            consumed = true;
        }

        // Write body content
        if (consumed && !httpRes.isCommitted ()) {
            if (httpRes.getContentType () == null) {
                httpRes.setContentType ("text/html; charset=utf-8");
            }
            httpRes.getOutputStream ().write (bodyContent.getBytes ("utf-8"));
        }

        // TODO this is an instance variable take care of multi-threading!
        handled = true;

        // TODO Merge logs and take care of method flow to log always
        if (loggable) {
            LOG.fine ("httpMethod:" + httpMethodStr + ", uri: " + uri);
            LOG.fine ("Time for request: " + (currentTimeMillis () - t));
        }
    }

    @SuppressWarnings ("unchecked")
    private String handleTargetRoute (
        HttpServletRequest aHttpReq, HttpServletResponse aHttpRes, String aBodyContent,
        RouteMatch aMatch, Route aTarget) {

        Request request = null;
        try {
            String result = null;
            if (!aTarget.isFilter ()) {
                request = new Request (aMatch, aHttpReq, aHttpRes);

                Object element = aTarget.handle (request);
                result = element != null? element.toString () : null;
            }
            if (result != null) {
                aBodyContent = result;
            }
        }
        catch (HaltException hEx) {
            throw hEx;
        }
        catch (Exception e) {
            Fault<Exception> handler = (Fault<Exception>)routeMatcher.findHandler (e.getClass ());
            if (handler != null && request != null) {
                handler.handle (e, request);
            }
            else {
                LOG.severe (e.getMessage ());
                aHttpRes.setStatus (SC_INTERNAL_SERVER_ERROR);
                aBodyContent = INTERNAL_ERROR;
            }
        }

        return aBodyContent;
    }

    /*
     * After and before are the same method except for HttpMethod.after|before
     */
    private String onFilter (
        final HttpMethod method,
        final HttpServletRequest httpRequest,
        final HttpServletResponse httpResponse,
        final String uri,
        final String acceptType,
        String bodyContent) {

        final List<RouteMatch> matchSet = routeMatcher.findTargets (method, uri, acceptType);

        for (RouteMatch filterMatch : matchSet) {
            final Request request = new Request (filterMatch, httpRequest, httpResponse);
            filterMatch.entry.handle (request);

            final String bodyAfterFilter = request.response.body ();
            if (bodyAfterFilter != null)
                bodyContent = bodyAfterFilter;
        }

        return bodyContent;
    }

    @Override public RouteMatcher getMatcher () { return routeMatcher; }

    @Override public void init (FilterConfig filterConfig) {
        // To be overriden in Servlet Filters
    }

    @Override public void destroy () {
        // Not used
    }
}
