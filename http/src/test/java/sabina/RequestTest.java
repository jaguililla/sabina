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

import static org.apache.http.HttpStatus.*;
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.testng.annotations.Test;
import sabina.route.RouteMatch;

public class RequestTest {

    private static final String THE_SERVLET_PATH = "/the/servlet/path";
    private static final String THE_CONTEXT_PATH = "/the/context/path";

    RouteMatch match = new RouteMatch (null, "/hi", "/hi");

    @Test public void queryParamShouldReturnsParametersFromQueryString () {
        Map<String, String[]> params = new HashMap<> ();
        params.put ("name", new String[] { "Federico" });
        HttpServletRequest servletRequest = new MockedHttpServletRequest (params);
        HttpServletResponse servletResponse = new MockedHttpServletResponse ();
        Request request = new Request (match, servletRequest, servletResponse);
        String name = request.queryParams ("name");
        assertEquals (name, "Federico", "Invalid name in query string");
    }

    @Test public void shouldBeAbleToGetTheServletPath () {
        HttpServletRequest servletRequest = new MockedHttpServletRequest (new HashMap<> ()) {
            @Override public String getServletPath () {
                return THE_SERVLET_PATH;
            }
        };
        HttpServletResponse servletResponse = new MockedHttpServletResponse ();
        Request request = new Request (match, servletRequest, servletResponse);
        assertEquals (
            request.servletPath (), THE_SERVLET_PATH,
            "Should have delegated getting the servlet path");
    }

    @Test public void shouldBeAbleToGetTheContextPath () {
        HttpServletRequest servletRequest = new MockedHttpServletRequest (new HashMap<> ()) {
            @Override public String getContextPath () {
                return THE_CONTEXT_PATH;
            }
        };
        HttpServletResponse servletResponse = new MockedHttpServletResponse ();
        Request request = new Request (match, servletRequest, servletResponse);
        assertEquals (
            request.contextPath (), THE_CONTEXT_PATH,
            "Should have delegated getting the context path");
    }

//    @Test (expectedExceptions = IllegalArgumentException.class)
//    public void contextWithNullRequest () {
//        new Request (null, new Response ());
//    }
//
//    @Test (expectedExceptions = IllegalArgumentException.class)
//    public void contextWithNullResponse () {
//        new Request (new Request (), null);
//    }
//
//    @Test (expectedExceptions = IllegalArgumentException.class)
//    public void contextWithNullRequestAndResponse () {
//        new Request (null, null);
//    }
//
//    @Test public void validContext () {
//        Request exchange = new Request (new Request (), new Response ());
//        assertTrue (exchange.request != null);
//        assertTrue (exchange.response != null);
//    }

    @Test (expectedExceptions = HaltException.class)
    public void halt () {
        try {
            Request exchange = createRequest ();
            exchange.halt ();
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_OK);
            assertEquals (he.body, null);
            throw he;
        }
    }

    @Test (expectedExceptions = HaltException.class)
    public void haltStatus () {
        try {
            Request exchange = createRequest ();
            exchange.halt (SC_ACCEPTED);
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_ACCEPTED);
            assertEquals (he.body, null);
            throw he;
        }
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void haltInvalidStatus () {
        Request exchange = createRequest ();
        exchange.halt (99);
    }

    @Test (expectedExceptions = HaltException.class)
    public void haltBody () {
        try {
            Request exchange = createRequest ();
            exchange.halt ("body");
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_OK);
            assertEquals (he.body, "body");
            throw he;
        }
    }

    @Test (expectedExceptions = HaltException.class)
    public void haltStatusAndBody () {
        try {
            Request exchange = createRequest ();
            exchange.halt (SC_ACCEPTED, "body");
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_ACCEPTED);
            assertEquals (he.body, "body");
            throw he;
        }
    }

    private static class MockedHttpServletRequest implements HttpServletRequest {
        private Map<String, String[]> params;

        public MockedHttpServletRequest (Map<String, String[]> params) {
            this.params = params;
        }

        @Override public String getAuthType () { return null; }
        @Override public String getContextPath () { return null; }
        @Override public Cookie[] getCookies () { return null; }
        @Override public long getDateHeader (String name) { return 0; }
        @Override public String getHeader (String name) { return null; }
        @Override public Enumeration<String> getHeaderNames () { return null; }
        @Override public Enumeration<String> getHeaders (String name) { return null; }
        @Override public int getIntHeader (String name) { return 0; }
        @Override public String getMethod () { return null; }
        @Override public String getPathInfo () { return null; }
        @Override public String getPathTranslated () { return null; }
        @Override public String getQueryString () { return null; }
        @Override public String getRemoteUser () { return null; }
        @Override public String getRequestURI () { return null; }
        @Override public StringBuffer getRequestURL () { return null; }
        @Override public String getRequestedSessionId () { return null; }
        @Override public String getServletPath () { return null; }
        @Override public HttpSession getSession () { return null; }
        @Override public HttpSession getSession (boolean create) { return null; }
        @Override public Principal getUserPrincipal () { return null; }
        @Override public boolean isRequestedSessionIdFromCookie () { return false; }
        @Override public boolean isRequestedSessionIdFromURL () { return false; }
        @Deprecated @Override public boolean isRequestedSessionIdFromUrl () { return false; }
        @Override public boolean isRequestedSessionIdValid () { return false; }
        @Override public boolean isUserInRole (String role) { return false; }
        @Override public Object getAttribute (String name) { return null; }
        @Override public Enumeration<String> getAttributeNames () { return null; }
        @Override public String getCharacterEncoding () { return null; }
        @Override public int getContentLength () { return 0; }
        @Override public String getContentType () { return null; }
        @Override public ServletInputStream getInputStream ()
            throws IOException { return null; }

        @Override public String getLocalAddr () { return null; }
        @Override public String getLocalName () { return null; }
        @Override public int getLocalPort () { return 0; }
        @Override public Locale getLocale () { return null; }
        @Override public Enumeration<Locale> getLocales () { return null; }
        @Override public String getParameter (String name) {
            return this.params.get (
                name)[0];
        }

        @Override public Map<String, String[]> getParameterMap () { return this.params; }
        @Override public Enumeration<String> getParameterNames () { return null; }
        @Override public String[] getParameterValues (String name) { return null; }
        @Override public String getProtocol () { return null; }
        @Override public BufferedReader getReader () throws IOException { return null; }
        @Deprecated @Override public String getRealPath (String path) { return null; }
        @Override public String getRemoteAddr () { return null; }
        @Override public String getRemoteHost () { return null; }
        @Override public int getRemotePort () { return 0; }
        @Override public RequestDispatcher getRequestDispatcher (String path) { return null; }
        @Override public String getScheme () { return null; }
        @Override public String getServerName () { return null; }
        @Override public int getServerPort () { return 0; }
        @Override public boolean isSecure () { return false; }
        @Override public void removeAttribute (String name) { /* do nothing */ }
        @Override public void setAttribute (String name, Object o) { /* do nothing */ }
        @Override public void setCharacterEncoding (String env)
            throws UnsupportedEncodingException { /* do nothing */ }

        @Override public ServletContext getServletContext () { return null; }
        @Override public AsyncContext startAsync ()
            throws IllegalStateException { return null; }

        @Override public AsyncContext startAsync (
            ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException { return null; }

        @Override public boolean isAsyncStarted () { return false; }
        @Override public boolean isAsyncSupported () { return false; }
        @Override public AsyncContext getAsyncContext () { return null; }
        @Override public DispatcherType getDispatcherType () { return null; }
        @Override public boolean authenticate (HttpServletResponse response)
            throws IOException, ServletException { return false; }

        @Override public void login (String username, String password)
            throws ServletException { /* do nothing */ }

        @Override public void logout () throws ServletException { /* do nothing */ }
        @Override public Collection<Part> getParts ()
            throws IOException, ServletException { return null; }

        @Override public Part getPart (String name)
            throws IOException, ServletException { return null; }

        @Override public String changeSessionId () { return null; }
        @Override public <T extends HttpUpgradeHandler> T upgrade (Class<T> handlerClass)
            throws IOException, ServletException { return null; }

        @Override public long getContentLengthLong () { return 0; }
    }

    private static class MockedHttpServletResponse implements HttpServletResponse {
        @Override public void addCookie (Cookie cookie) {}
        @Override public boolean containsHeader (String name) { return false; }
        @Override public String encodeURL (String url) { return null; }
        @Override public String encodeRedirectURL (String url) { return null; }
        @Deprecated @Override public String encodeUrl (String url) { return null; }
        @Deprecated @Override public String encodeRedirectUrl (String url) { return null; }
        @Override public void sendError (int sc, String msg) throws IOException {}
        @Override public void sendError (int sc) throws IOException {}
        @Override public void sendRedirect (String location) throws IOException {}
        @Override public void setDateHeader (String name, long date) {}
        @Override public void addDateHeader (String name, long date) {}
        @Override public void setHeader (String name, String value) {}
        @Override public void addHeader (String name, String value) {}
        @Override public void setIntHeader (String name, int value) {}
        @Override public void addIntHeader (String name, int value) {}
        @Override public void setStatus (int sc) {}
        @Deprecated @Override public void setStatus (int sc, String sm) {}
        @Override public int getStatus () { return 0; }
        @Override public String getHeader (String name) { return null; }
        @Override public Collection<String> getHeaders (String name) { return null; }
        @Override public Collection<String> getHeaderNames () { return null; }
        @Override public String getCharacterEncoding () { return null; }
        @Override public String getContentType () { return null; }
        @Override public ServletOutputStream getOutputStream () throws IOException { return null; }
        @Override public PrintWriter getWriter () throws IOException { return null; }
        @Override public void setCharacterEncoding (String charset) {}
        @Override public void setContentLength (int len) {}
        @Override public void setContentLengthLong (long len) {}
        @Override public void setContentType (String type) {}
        @Override public void setBufferSize (int size) {}
        @Override public int getBufferSize () { return 0; }
        @Override public void flushBuffer () throws IOException {}
        @Override public void resetBuffer () {}
        @Override public boolean isCommitted () { return false; }
        @Override public void reset () {}
        @Override public void setLocale (Locale loc) {}
        @Override public Locale getLocale () { return null; }
    }

    private Request createRequest () {
        RouteMatch match = new RouteMatch (null, "/", "/");
        MockedHttpServletRequest servletRequest = new MockedHttpServletRequest (null);
        MockedHttpServletResponse servletResponse = new MockedHttpServletResponse ();
        return new Request (match, servletRequest, servletResponse);
    }
}
