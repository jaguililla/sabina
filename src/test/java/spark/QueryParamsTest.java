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

package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class QueryParamsTest {

    QueryParams queryMap = new QueryParams ();

    @Test public void constructorWithParametersMap () {
        Map<String, String[]> params = new HashMap<> ();

        params.put ("user[info][name]", new String[] { "fede" });

        QueryParams queryMap = new QueryParams (params);

        assertEquals ("fede", queryMap.get ("user").get ("info").get ("name").value ());
        assertEquals ("fede", queryMap.get ("user", "info", "name").value ());
    }

    @Test public void keyToMap () {
        QueryParams queryMap = new QueryParams ();

        queryMap.loadKeys ("user[info][first_name]", new String[] { "federico" });
        queryMap.loadKeys ("user[info][last_name]", new String[] { "dayan" });

        assertFalse (queryMap.getQueryMap ().isEmpty ());
        assertFalse (queryMap.getQueryMap ().get ("user").getQueryMap ().isEmpty ());
        assertFalse (
            queryMap.getQueryMap ().get ("user").getQueryMap ().get ("info").getQueryMap ()
                .isEmpty ());
        assertEquals ("federico",
            queryMap.getQueryMap ().get ("user").getQueryMap ().get ("info").getQueryMap ()
                .get ("first_name").getValues ()[0]);
        assertEquals ("dayan",
            queryMap.getQueryMap ().get ("user").getQueryMap ().get ("info").getQueryMap ()
                .get ("last_name").getValues ()[0]);

        assertTrue (queryMap.hasKeys ());
        assertFalse (queryMap.hasValue ());
        assertTrue (
            queryMap.getQueryMap ().get ("user").getQueryMap ().get ("info").getQueryMap ()
                .get ("last_name").hasValue ());
    }

    @Test public void testDifferentTypesForValue () {
        QueryParams queryMap = new QueryParams ();

        queryMap.loadKeys ("user[age]", new String[] { "10" });
        queryMap.loadKeys ("user[agrees]", new String[] { "true" });

        assertEquals (new Integer (10), queryMap.get ("user").get ("age").integerValue ());
        assertEquals (new Float (10), queryMap.get ("user").get ("age").floatValue ());
        assertEquals (new Double (10), queryMap.get ("user").get ("age").doubleValue ());
        assertEquals (new Long (10), queryMap.get ("user").get ("age").longValue ());
        assertEquals (Boolean.TRUE, queryMap.get ("user").get ("agrees").booleanValue ());
    }

    @Test
    public void parseKeyShouldParseRootKey () {
        String[] parsed = queryMap.parseKey ("user[name][more]");

        assertEquals ("user", parsed[0]);
        assertEquals ("[name][more]", parsed[1]);
    }

    @Test
    public void parseKeyShouldParseSubkeys () {
        String[] parsed = null;

        parsed = queryMap.parseKey ("[name][more]");

        assertEquals ("name", parsed[0]);
        assertEquals ("[more]", parsed[1]);

        parsed = queryMap.parseKey ("[more]");

        assertEquals ("more", parsed[0]);
        assertEquals ("", parsed[1]);
    }

    @Test
    public void itShouldbeNullSafe () {
        QueryParams queryParams = new QueryParams ();

        String ret = queryParams.get ("x").get ("z").get ("y").value ("w");

        assertNull (ret);
    }

    @Test
    public void testConstructor () {
        QueryParams queryMap = new QueryParams ("user[name][more]", "fede");

        assertFalse (queryMap.getQueryMap ().isEmpty ());
        assertFalse (queryMap.getQueryMap ().get ("user").getQueryMap ().isEmpty ());
        assertFalse (
            queryMap.getQueryMap ().get ("user").getQueryMap ().get ("name").getQueryMap ()
                .isEmpty ());
        assertEquals ("fede",
            queryMap.getQueryMap ().get ("user").getQueryMap ().get ("name").getQueryMap ()
                .get ("more").getValues ()[0]);
    }

    @Test
    public void testToMap () {
        Map<String, String[]> params = new HashMap<> ();

        params.put ("user[info][name]", new String[] { "fede" });
        params.put ("user[info][last]", new String[] { "dayan" });

        QueryParams queryMap = new QueryParams (params);

        Map<String, String[]> map = queryMap.get ("user", "info").toMap ();

        assertEquals (2, map.size ());
        assertEquals ("fede", map.get ("name")[0]);
        assertEquals ("dayan", map.get ("last")[0]);
    }
}
