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

package sabina.examples;

import static java.lang.String.format;
import static sabina.Sabina.*;

/**
 * Demonstrate the use of sessions inside Sabina.
 *
 * @author Per Wendel
 */
final class SessionExample {
    private static final String SESSION_NAME = "username";

    public static void main (String[] args) {
        get ("/", it ->
            it.session ().<String>attribute (SESSION_NAME) == null?
                "<html>" +
                "    <body>" +
                "        What's your name?:" +
                "        <form action=\"/entry\" method=\"POST\">" +
                "            <input type=\"text\" name=\"name\"/>" +
                "            <input type=\"submit\" value=\"go\"/>" +
                "        </form>" +
                "    </body>" +
                "</html>"
                :
                format (
                    "<html><body>Hello, %s!</body></html>",
                    it.session ().<String>attribute (SESSION_NAME))
        );

        post ("/entry", it -> {
            String name = it.queryParams ("name");
            if (name != null)
                it.session ().attribute (SESSION_NAME, name);

            it.redirect ("/");
        });

        get ("/clear", it -> {
            it.session ().removeAttribute (SESSION_NAME);
            it.redirect ("/");
        });

        start ();
    }
}
