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

package sabina

import static org.apache.http.HttpStatus.*
import static sabina.HttpMethod.AFTER

import sabina.Router.Handler

import javax.servlet.http.*

import org.testng.annotations.Test

@Test public class RequestTest {
    private static final String THE_SERVLET_PATH = "/the/servlet/path"
    private static final String THE_CONTEXT_PATH = "/the/context/path"

    private RouteMatch match = new RouteMatch (new Route (AFTER, "/hi", { "" } as Handler), "/hi")

    public void queryParamShouldReturnsParametersFromQueryString () {
        HttpServletRequest servletRequest = [
            getParameter : { "Federico" }
        ] as HttpServletRequest

        Request request = new Request (match, servletRequest, null)
        assert request.queryParams ("name") == "Federico"
    }

    public void shouldBeAbleToGetTheServletPath () {
        HttpServletRequest servletRequest = [
            getServletPath : { THE_SERVLET_PATH }
        ] as HttpServletRequest

        Request request = new Request (match, servletRequest, null)
        assert request.servletPath () == THE_SERVLET_PATH
    }

    public void shouldBeAbleToGetTheContextPath () {
        HttpServletRequest servletRequest = [
            getContextPath : { THE_CONTEXT_PATH }
        ] as HttpServletRequest

        Request request = new Request (match, servletRequest, null)
        assert request.contextPath () == THE_CONTEXT_PATH
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

    @Test (expectedExceptions = EndException)
    public void halt () {
        try {
            Request exchange = createRequest ()
            exchange.halt ()
        }
        catch (EndException he) {
            assert he.statusCode == SC_OK
            assert he.body == null
            throw he
        }
    }

    @Test (expectedExceptions = EndException)
    public void haltStatus () {
        try {
            Request exchange = createRequest ()
            exchange.halt (SC_ACCEPTED)
        }
        catch (EndException he) {
            assert he.statusCode == SC_ACCEPTED
            assert he.body == null
            throw he
        }
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void haltInvalidStatus () {
        Request exchange = createRequest ()
        exchange.halt (99)
    }

    @Test (expectedExceptions = EndException)
    public void haltBody () {
        try {
            Request exchange = createRequest ()
            exchange.halt ("body")
        }
        catch (EndException he) {
            assert he.statusCode == SC_OK
            assert he.body == "body"
            throw he
        }
    }

    @Test (expectedExceptions = EndException)
    public void haltStatusAndBody () {
        try {
            Request exchange = createRequest ()
            exchange.halt (SC_ACCEPTED, "body")
        }
        catch (EndException he) {
            assert he.statusCode == SC_ACCEPTED
            assert he.body == "body"
            throw he
        }
    }

    private static Request createRequest (String route = "/", String request = "/") {
        RouteMatch match = new RouteMatch (new Route (HttpMethod.GET, route, { "" } as
            Handler), request)
        return new Request (match, [] as HttpServletRequest, [] as HttpServletResponse)
    }
}
