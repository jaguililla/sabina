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

package spark.examples;

import static spark.Spark.get;
import static spark.Spark.post;

class SessionExample {
    private static final String SESSION_NAME = "username";

    public static void main (String[] args) {
        get ("/", it -> {
            String name = it.session ().attribute (SESSION_NAME);
            if (name == null) {
                return
                    "<html>" +
                        "    <body>" +
                        "        What's your name?:" +
                        "        <form action=\"/entry\" method=\"POST\">" +
                        "            <input type=\"text\" name=\"name\"/>" +
                        "            <input type=\"submit\" value=\"go\"/>" +
                        "        </form>" +
                        "    </body>" +
                        "</html>";
            }
            else {
                return String.format ("<html><body>Hello, %s!</body></html>", name);
            }
        });

        post ("/entry", it -> {
            String name = it.queryParams ("name");
            if (name != null) {
                it.session ().attribute (SESSION_NAME, name);
            }
            it.redirect ("/");
            return null;
        });

        get ("/clear", it -> {
            it.session ().removeAttribute (SESSION_NAME);
            it.redirect ("/");
            return null;
        });
    }
}