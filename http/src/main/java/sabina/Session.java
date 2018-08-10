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

import static co.there4.bali.Checks.require;

import java.util.*;

import javax.servlet.http.HttpSession;

/**
 * Provides session information.
 */
public final class Session {
    private final HttpSession session;

    /**
     * Creates a session with the <code>HttpSession</code>.
     *
     * @param session Session implementation.
     *
     * @throws IllegalArgumentException If the session is null.
     */
    Session (final HttpSession session) {
        require (session != null, "Session cannot be null");
        this.session = session;
    }

    /**
     * Returns the object bound with the specified name in this session, or null if no object
     * is bound under the name.
     *
     * @param name A string specifying the name of the object.
     * @param <T>  The type parameter.
     * @return The object with the specified name.
     */
    @SuppressWarnings("unchecked") public <T> T attribute (final String name) {
        return (T)session.getAttribute (name);
    }

    /**
     * Binds an object to this session, using the name specified.
     *
     * @param name The name to which the object is bound; cannot be null.
     * @param value The object to be bound.
     */
    public void attribute (final String name, final Object value) {
        session.setAttribute (name, value);
    }

    public Map<String, Object> attributes () {
        final Map<String, Object> attributes = new HashMap<> ();
        final Enumeration<String> enumeration = session.getAttributeNames ();
        while (enumeration.hasMoreElements ()) {
            final String key = enumeration.nextElement ();
            attributes.put (key, attribute (key));
        }

        return attributes;
    }

    public Set<String> attributeValues () {
        final TreeSet<String> attributes = new TreeSet<> ();
        final Enumeration<String> enumeration = session.getAttributeNames ();
        while (enumeration.hasMoreElements ())
            attributes.add (attribute (enumeration.nextElement ()).toString ());

        return attributes;
    }

    /**
     * @return The time when this session was created, measured in milliseconds since midnight
     * January 1, 1970 GMT.
     */
    public long creationTime () {
        return session.getCreationTime ();
    }

    /**
     * @return A string containing the unique identifier assigned to this session.
     */
    public String id () {
        return session.getId ();
    }

    /**
     * @return The last time the client sent a request associated with this session, as the
     * number of milliseconds since midnight January 1, 1970 GMT, and marked by the time the
     * container received the request.
     */
    public long lastAccessedTime () {
        return session.getLastAccessedTime ();
    }

    /**
     * @return The maximum time interval, in seconds, that the container will keep this session
     * open between client accesses.
     */
    public int maxInactiveInterval () {
        return session.getMaxInactiveInterval ();
    }

    /**
     * Specifies the time, in seconds, between client requests the web container will
     * invalidate this session.
     *
     * @param interval The interval.
     */
    public void maxInactiveInterval (final int interval) {
        session.setMaxInactiveInterval (interval);
    }

    public void invalidate () {
        session.invalidate ();
    }

    public boolean isNew () {
        return session.isNew ();
    }

    public void removeAttribute (final String name) {
        session.removeAttribute (name);
    }

    public Enumeration<String> attributeNames () {
        return session.getAttributeNames ();
    }
}
