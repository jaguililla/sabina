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

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

/**
 * Provides session information.
 */
public final class Session {
    private HttpSession session;

    /**
     * Creates a session with the <code>HttpSession</code>.
     *
     * @param session Session implementation.
     *
     * @throws IllegalArgumentException If the session is null.
     */
    Session (HttpSession session) {
        if (session == null)
            throw new IllegalArgumentException ("Session cannot be null");

        this.session = session;
    }

    /**
     * @return The raw <code>HttpSession</code> object handed in by the servlet container.
     */
    public HttpSession raw () {
        return session;
    }

    /**
     * Returns the object bound with the specified name in this session, or null if no object
     * is bound under the name.
     *
     * @param name A string specifying the name of the object.
     * @param <T>  The type parameter.
     * @return The object with the specified name.
     */
    public <T> T attribute (String name) {
        return (T)session.getAttribute (name);
    }

    /**
     * Binds an object to this session, using the name specified.
     *
     * @param name The name to which the object is bound; cannot be null.
     * @param value The object to be bound.
     */
    public void attribute (String name, Object value) {
        session.setAttribute (name, value);
    }

    /**
     * @return An <code>Enumeration</code> of <code>String</code> objects containing the names
     * of all the objects bound to this session.
     */
    public Set<String> attributes () {
        TreeSet<String> attributes = new TreeSet<> ();
        Enumeration<String> enumeration = session.getAttributeNames ();
        while (enumeration.hasMoreElements ())
            attributes.add (enumeration.nextElement ());

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
    public void maxInactiveInterval (int interval) {
        session.setMaxInactiveInterval (interval);
    }

    /**
     * Invalidates this session then unbinds any objects bound to it.
     */
    public void invalidate () {
        session.invalidate ();
    }

    /**
     * @return True if the client does not yet know about the session or if the client chooses
     * not to join the session.
     */
    public boolean isNew () {
        return session.isNew ();
    }

    /**
     * Removes the object bound with the specified name from this session.
     *
     * @param name The name of the object to remove from this session.
     */
    public void removeAttribute (String name) {
        session.removeAttribute (name);
    }
}
