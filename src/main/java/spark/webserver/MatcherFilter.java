/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spark.webserver;

import static java.lang.System.currentTimeMillis;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Access;
import spark.HaltException;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.Route;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;

/**
 * Filter for matching of filters and routes.
 *
 * @author Per Wendel
 */
public class MatcherFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger (MatcherFilter.class);

    private static final String
        ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept",
        INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>",
        NOT_FOUND =
            "<html><body>" +
            "<h2>404 Not found</h2>The requested route [%s] has not been mapped in Spark" +
            "</body></html>";

    private final RouteMatcher routeMatcher;
    private final boolean isServletContext, hasOtherHandlers;

    /**
     * Constructor.
     *
     * @param routeMatcher The route matcher
     * @param isServletContext If true, chain.doFilter will be invoked if request is not
     * consumed by Spark.
     * @param hasOtherHandlers If true, do nothing if request is not consumed by Spark in order
     * to let others handlers process the request.
     */
    public MatcherFilter (
        RouteMatcher routeMatcher, boolean isServletContext, boolean hasOtherHandlers) {

        this.routeMatcher = routeMatcher;
        this.isServletContext = isServletContext;
        this.hasOtherHandlers = hasOtherHandlers;
    }

    public MatcherFilter (boolean isServletContext, boolean hasOtherHandlers) {
        this (RouteMatcherFactory.get (), isServletContext, hasOtherHandlers);
    }

    @Override public void doFilter (
        ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {

        long t = currentTimeMillis ();

        HttpServletRequest httpReq = (HttpServletRequest)servletRequest;
        HttpServletResponse httpRes = (HttpServletResponse)servletResponse;

        String uri = httpReq.getRequestURI ();
        String httpMethodStr = httpReq.getMethod ().toLowerCase ();
        String acceptType = httpReq.getHeader (ACCEPT_TYPE_REQUEST_MIME_HEADER);
        String bodyContent = null;

        try {
            RequestWrapper req = new RequestWrapper ();
            ResponseWrapper res = new ResponseWrapper ();

            bodyContent =
                beforeFilters (httpReq, httpRes, uri, acceptType, bodyContent, req, res);

            HttpMethod httpMethod = HttpMethod.valueOf (httpMethodStr);

            RouteMatch match =
                routeMatcher.findTargetForRequestedRoute (httpMethod, uri, acceptType);

            Object target = null;
            if (match != null) {
                target = match.getTarget ();
            }
            else if (httpMethod == HttpMethod.head && bodyContent == null) {
                // See if get is mapped to provide default head mapping
                RouteMatch requestedRouteTarget =
                    routeMatcher.findTargetForRequestedRoute (HttpMethod.get, uri, acceptType);
                bodyContent = requestedRouteTarget != null? "" : null;
            }

            if (target != null) {
                bodyContent =
                    handleTargetRoute (httpReq, httpRes, bodyContent, req, res, match, target);
            }

            bodyContent =
                afterFilters (httpReq, httpRes, uri, acceptType, bodyContent, req, res);
        }
        catch (HaltException hEx) {
            LOG.debug ("halt performed");
            httpRes.setStatus (hEx.getStatusCode ());
            String haltBody = hEx.getBody ();
            bodyContent = (haltBody != null)? haltBody : "";
        }

        boolean consumed = bodyContent != null;

        if (!consumed && hasOtherHandlers)
            throw new NotConsumedException ();

        if (!consumed && !isServletContext) {
            httpRes.setStatus (HttpServletResponse.SC_NOT_FOUND);
            bodyContent = String.format (NOT_FOUND, uri);
            consumed = true;
        }

        // Write body content
        if (consumed && !httpRes.isCommitted ()) {
            if (httpRes.getContentType () == null) {
                httpRes.setContentType ("text/html; charset=utf-8");
            }
            httpRes.getOutputStream ().write (bodyContent.getBytes ("utf-8"));
        }
        else if (chain != null) {
            chain.doFilter (httpReq, httpRes);
        }

        // TODO Merge logs and take care of method flow to log always
        LOG.debug ("httpMethod:" + httpMethodStr + ", uri: " + uri);
        LOG.debug ("Time for request: " + (currentTimeMillis () - t));
    }

    private String handleTargetRoute (
        HttpServletRequest aHttpReq, HttpServletResponse aHttpRes, String aBodyContent,
        RequestWrapper aReq, ResponseWrapper aRes, RouteMatch aMatch, Object aTarget) {

        try {
            String result = null;
            if (aTarget instanceof Route) {
                Route route = ((Route)aTarget);
                Request request = RequestResponseFactory.create (aMatch, aHttpReq);
                Response response = RequestResponseFactory.create (aHttpRes);

                aReq.setDelegate (request);
                aRes.setDelegate (response);

                Object element = route.handle (aReq, aRes);
                result = route.render (element);
            }
            if (result != null) {
                aBodyContent = result;
            }
        }
        catch (HaltException hEx) {
            throw hEx;
        }
        catch (Exception e) {
            LOG.error ("", e);
            aHttpRes.setStatus (SC_INTERNAL_SERVER_ERROR);
            aBodyContent = INTERNAL_ERROR;
        }

        return aBodyContent;
    }

    /*
     * After and before are the same method except for HttpMethod.after|before
     */
    private String afterFilters (
        HttpServletRequest aHttpRequest, HttpServletResponse aHttpResponse, String aUri,
        String aAcceptType, String aBodyContent, RequestWrapper aReq, ResponseWrapper aRes) {

        List<RouteMatch> matchSet =
            routeMatcher.findTargetsForRequestedRoute (HttpMethod.after, aUri, aAcceptType);

        for (RouteMatch filterMatch : matchSet) {
            Object filterTarget = filterMatch.getTarget ();
            if (filterTarget instanceof spark.Filter) {
                Request request = RequestResponseFactory.create (filterMatch, aHttpRequest);
                Response response = RequestResponseFactory.create (aHttpResponse);

                aReq.setDelegate (request);
                aRes.setDelegate (response);

                spark.Filter filter = (spark.Filter)filterTarget;
                filter.handle (aReq, aRes);

                String bodyAfterFilter = Access.getBody (response);
                if (bodyAfterFilter != null)
                    aBodyContent = bodyAfterFilter;
            }
        }
        return aBodyContent;
    }

    private String beforeFilters (
        HttpServletRequest aHttpRequest, HttpServletResponse aHttpResponse, String aUri,
        String aAcceptType, String aBodyContent, RequestWrapper aReq, ResponseWrapper aRes) {

        List<RouteMatch> matchSet =
            routeMatcher.findTargetsForRequestedRoute (HttpMethod.before, aUri, aAcceptType);

        for (RouteMatch filterMatch : matchSet) {
            Object filterTarget = filterMatch.getTarget ();
            if (filterTarget instanceof spark.Filter) {
                Request request = RequestResponseFactory.create (filterMatch, aHttpRequest);
                Response response = RequestResponseFactory.create (aHttpResponse);

                aReq.setDelegate (request);
                aRes.setDelegate (response);

                spark.Filter filter = (spark.Filter)filterTarget;
                filter.handle (aReq, aRes);

                String bodyAfterFilter = Access.getBody (response);
                if (bodyAfterFilter != null)
                    aBodyContent = bodyAfterFilter;
            }
        }

        return aBodyContent;
    }

    @Override public void init (FilterConfig filterConfig) {
        // Not used
    }

    @Override public void destroy () {
        // Not used
    }
}
