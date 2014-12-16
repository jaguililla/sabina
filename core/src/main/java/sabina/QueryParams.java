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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

/**
 * This objects represent the parameters sent on a Http Request. Parses parameters keys like in
 * Sinatra.
 *
 * For a querystring like:
 *
 * <code>user[name]=federico&#38;user[lastname]=dayan</code>
 *
 * We get would get a structure like:
 *
 * <code>user : {name: federico, lastname: dayan}</code>
 *
 * That is:
 *
 * <code>
 * queryParamsMapInstance.get("user).get("name").value();
 * queryParamsMapInstance.get("user).get("lastname").value();
 * </code>
 *
 * It is null safe, meaning that if a key does not exist, it does not throw
 * NullPointerException, it just returns null.
 *
 * @author fddayan
 */
public class QueryParams {
    private static final class NullQueryParams extends QueryParams {
        public NullQueryParams () { super (); }
    }

    private static final QueryParams NULL = new NullQueryParams ();

    /** Holds the nested keys */
    private final Map<String, QueryParams> queryMap = new HashMap<> ();

    /** Value(s) for this key */
    private String[] values;

    private final Pattern p = Pattern.compile ("\\A[\\[\\]]*([^\\[\\]]+)\\]*");

    /**
     * Creates a new QueryParamsMap from and HttpServletRequest. <br>
     * Parses the parameters from request.getParameterMap() <br>
     * No need to decode, since HttpServletRequest does it for us.
     *
     * @param request the servlet request
     */
    QueryParams (HttpServletRequest request) {
        checkArgument (request != null);
        assert request != null;

        loadQueryString (request.getParameterMap ());
    }

    // Just for testing
    QueryParams () { super (); }

    /**
     * Parses the key and creates the child QueryParamMaps
     * <p>
     * user[info][name] creates 3 nested QueryParamMaps. For user, info and
     * name.
     *
     * @param key The key in the formar fo key1[key2][key3] (for example:
     * user[info][name]).
     * @param values the values
     */
    QueryParams (String key, String... values) {
        loadKeys (key, values);
    }

    QueryParams (Map<String, String[]> params) {
        loadQueryString (params);
    }

    /**
     * loads query string
     *
     * @param params the parameters
     */
    protected final void loadQueryString (Map<String, String[]> params) {
        for (Map.Entry<String, String[]> param : params.entrySet ())
            loadKeys (param.getKey (), param.getValue ());
    }

    /**
     * loads keys
     *
     * @param key   the key
     * @param value the values
     */
    protected final void loadKeys (String key, String[] value) {
        String[] parsed = parseKey (key);

        if (parsed == null)
            return;

        if (!queryMap.containsKey (parsed[0]))
            queryMap.put (parsed[0], new QueryParams ());
        if (!parsed[1].isEmpty ())
            queryMap.get (parsed[0]).loadKeys (parsed[1], value);
        else
            queryMap.get (parsed[0]).values = value.clone ();
    }

    protected final String[] parseKey (String key) {
        Matcher m = p.matcher (key);

        return m.find ()?
            new String[] { cleanKey (m.group ()), key.substring (m.end ()) } :
            null;
    }

    protected final String cleanKey (String group) {
        return group.startsWith ("[")?
            group.substring (1, group.length () - 1) :
            group;
    }

    /**
     * Returns and element from the specified key. <br>
     * For querystring: <br>
     * <br>
     * <code>
     * user[name]=fede
     * get("user").get("name").value() #  fede
     * or
     * get("user","name").value() #  fede
     * </code>
     *
     * @param keys The parameter nested key(s)
     * @return the query params map
     */
    public QueryParams get (String... keys) {
        QueryParams ret = this;

        for (String key : keys)
            ret = ret.queryMap.containsKey (key)? ret.queryMap.get (key) : NULL;

        return ret;
    }

    /**
     * Returns the value for this key. <br>
     * If this key has nested elements and does not have a value returns null.
     *
     * @return the value
     */
    public String value () {
        return hasValue ()? values[0] : null;
    }

    /**
     * Returns the value for that key. <br>
     * <p>
     * It is a shortcut for: <br>
     * <br>
     * <code>
     * get("user").get("name").value()
     * get("user").value("name")
     * </code>
     *
     * @param keys the key(s)
     * @return the value
     */
    public String value (String... keys) {
        return get (keys).value ();
    }

    /**
     * @return has keys
     */
    public boolean hasKeys () {
        return !this.queryMap.isEmpty ();
    }

    /**
     * @return has values
     */
    public boolean hasValue () {
        return this.values != null && this.values.length > 0;
    }

    /**
     * @return the boolean value
     */
    public Boolean booleanValue () {
        return hasValue ()? Boolean.valueOf (value ()) : null;
    }

    /**
     * @return the integer value
     */
    public Integer integerValue () {
        return hasValue ()? Integer.valueOf (value ()) : null;
    }

    /**
     * @return the long value
     */
    public Long longValue () {
        return hasValue ()? Long.valueOf (value ()) : null;
    }

    /**
     * @return the float value
     */
    public Float floatValue () {
        return hasValue ()? Float.valueOf (value ()) : null;
    }

    /**
     * @return the double value
     */
    public Double doubleValue () {
        return hasValue ()? Double.valueOf (value ()) : null;
    }

    /**
     * @return the values
     */
    public String[] values () {
        return this.values.clone ();
    }

    /**
     * @return the queryMap
     */
    Map<String, QueryParams> getQueryMap () { return queryMap; }

    /**
     * @return the values
     */
    String[] getValues () { return values; }

    /**
     * @return Map representation
     */
    public Map<String, String[]> toMap () {
        Map<String, String[]> map = new HashMap<> ();

        for (Entry<String, QueryParams> key : this.queryMap.entrySet ())
            map.put (key.getKey (), key.getValue ().values);

        return map;
    }
}
