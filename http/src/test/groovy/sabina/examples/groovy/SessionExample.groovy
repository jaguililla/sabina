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

package sabina.examples.groovy

import sabina.Request
import sabina.Router
import sabina.Server

import static sabina.util.Configuration.configuration
import static sabina.util.Configuration.parameters

/**
 * Demonstrate the use of sessions inside Sabina.
 *
 * @author Per Wendel
 */
final class SessionExample extends Server {
    private static final String SESSION_NAME = "username"

    static {
        Request.metaClass.getSession {
            delegate.session ()
        }
    }

    void get (String path, Closure<Object> handler) {
        super.get (path, new Router.Handler () {
            @Override Object apply (Request request) {
                handler.delegate = request
                handler (request)
            }
        })
    }

    void post (String path, Closure<Object> handler) {
        super.post (path, new Router.Handler () {
            @Override Object apply (Request request) {
                handler.delegate = request
                handler (request)
            }
        })
    }

    def d = get "/", {
        Object sessionName = it.session.attribute (SESSION_NAME)

        sessionName == null?
            """
                <html>
                    <body>
                        What's your name?:
                        <form action="/entry" method="POST">
                            <input type="text" name="name"/>
                            <input type="submit" value="go"/>
                        </form>
                    </body>
                </html>
                """ :
            "<html><body>Hello, ${sessionName}!</body></html>"
    }

    SessionExample () {
        configuration ().load (
            parameters ("--parameter1", "value", "--argument2", "another value")
        )

        get "/", {
            Object sessionName = it.session.attribute (SESSION_NAME)

            sessionName == null?
                """
                <html>
                    <body>
                        What's your name?:
                        <form action="/entry" method="POST">
                            <input type="text" name="name"/>
                            <input type="submit" value="go"/>
                        </form>
                    </body>
                </html>
                """ :
                "<html><body>Hello, ${sessionName}!</body></html>"
        }

        post "/entry", {
            String name = it.queryParams ("name")
            if (name != null)
                it.session.attribute (SESSION_NAME, name)

            it.redirect "/"
        }

        get "/clear", {
            it.session.removeAttribute (SESSION_NAME)
            it.redirect "/"
        }

        start ()
    }

    static void main (String... args) {
        new SessionExample ();
    }
}
