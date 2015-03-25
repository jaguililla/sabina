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

package sabina.integration;

import sabina.Server;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
final class Cookies {
    public static void setup (Server s) {
        s.post ("assertNoCookies", it -> {
            if (!it.cookies ().isEmpty ()) {
                it.halt (500);
            }
        });

        s.post ("setCookie", it -> {
            it.cookie (it.queryParams ("cookieName"), it.queryParams ("cookieValue"));
        });

        s.post ("assertHasCookie", it -> {
            String cookieValue = it.cookie (it.queryParams ("cookieName"));
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
        });

        s.post ("removeCookie", it -> {
            String cookieName = it.queryParams ("cookieName");
            String cookieValue = it.cookie (cookieName);
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            it.removeCookie (cookieName);
        });
    }

    static void emptyCookies (TestScenario testScenario) {
        testScenario.doPost ("/assertNoCookies");
    }

    static void createCookie (TestScenario testScenario) {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        testScenario.doPost ("/setCookie?cookieName=" + cookie);
        testScenario.doPost ("/assertHasCookie?cookieName=" + cookie);
    }

    static void removeCookie (TestScenario testScenario) {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        testScenario.doPost ("/setCookie?cookieName=" + cookie);
        testScenario.doPost ("/removeCookie?cookieName=" + cookie);
        testScenario.doPost ("/assertNoCookies");
    }
}
