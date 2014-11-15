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

public class ContextTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void contextWithNullRequest () {
        new Context (null, new Response ());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void contextWithNullResponse () {
        new Context (new Request (), null);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void contextWithNullRequestAndResponse () {
        new Context (null, null);
    }

    @Test public void validContext () {
        Context context = new Context (new Request (), new Response ());
        assertTrue (context.request != null);
        assertTrue (context.response != null);
    }

    @Test (expectedExceptions = HaltException.class)
    public void halt () {
        try {
            Context context = new Context (new Request (), new Response ());
            context.halt ();
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
            Context context = new Context (new Request (), new Response ());
            context.halt (SC_ACCEPTED);
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_ACCEPTED);
            assertEquals (he.body, null);
            throw he;
        }
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void haltInvalidStatus () {
        Context context = new Context (new Request (), new Response ());
        context.halt (99);
    }

    @Test (expectedExceptions = HaltException.class)
    public void haltBody () {
        try {
            Context context = new Context (new Request (), new Response ());
            context.halt ("body");
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
            Context context = new Context (new Request (), new Response ());
            context.halt (SC_ACCEPTED, "body");
        }
        catch (HaltException he) {
            assertEquals (he.statusCode, SC_ACCEPTED);
            assertEquals (he.body, "body");
            throw he;
        }
    }
}
