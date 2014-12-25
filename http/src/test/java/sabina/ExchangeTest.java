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

import static javax.servlet.http.HttpServletResponse.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class ExchangeTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void contextWithNullRequest () {
        new Exchange (null, new Response ());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void contextWithNullResponse () {
        new Exchange (new Request (), null);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void contextWithNullRequestAndResponse () {
        new Exchange (null, null);
    }

    @Test public void validContext () {
        Exchange exchange = new Exchange (new Request (), new Response ());
        assertTrue (exchange.request != null);
        assertTrue (exchange.response != null);
    }

    @Test (expectedExceptions = HaltException.class)
    public void halt () {
        try {
            Exchange exchange = new Exchange (new Request (), new Response ());
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
            Exchange exchange = new Exchange (new Request (), new Response ());
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
        Exchange exchange = new Exchange (new Request (), new Response ());
        exchange.halt (99);
    }

    @Test (expectedExceptions = HaltException.class)
    public void haltBody () {
        try {
            Exchange exchange = new Exchange (new Request (), new Response ());
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
            Exchange exchange = new Exchange (new Request (), new Response ());
            exchange.halt (SC_ACCEPTED, "body");
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_ACCEPTED);
            assertEquals (he.body, "body");
            throw he;
        }
    }
}
