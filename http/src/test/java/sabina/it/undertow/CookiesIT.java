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

package sabina.it.undertow;

import static sabina.Sabina.*;
import static sabina.util.TestUtil.resetBackend;
import static sabina.util.TestUtil.setBackend;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.util.TestUtil;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
@Test public class CookiesIT {
    private static TestUtil testUtil = new TestUtil ();

    @BeforeClass public static void setup () throws InterruptedException {
        setBackend ("undertow");

        post ("assertNoCookies", it -> {
            if (!it.cookies ().isEmpty ()) {
                it.halt (500);
            }
            return "";
        });

        post ("setCookie", it -> {
            it.cookie (it.queryParams ("cookieName"), it.queryParams ("cookieValue"));
            return "";
        });

        post ("assertHasCookie", it -> {
            String cookieValue = it.cookie (it.queryParams ("cookieName"));
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            return "";
        });

        post ("removeCookie", it -> {
            String cookieName = it.queryParams ("cookieName");
            String cookieValue = it.cookie (cookieName);
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            it.removeCookie (cookieName);
            return "";
        });

        start (testUtil.getPort ());
        testUtil.waitForStartup ();
        resetBackend ();
    }

    @AfterClass public static void cleanup () {
        stop ();
        testUtil.waitForShutdown ();
    }

    public void emptyCookies () {
        testUtil.doPost ("/assertNoCookies");
    }

    public void createCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        testUtil.doPost ("/setCookie?cookieName=" + cookie);
        testUtil.doPost ("/assertHasCookie?cookieName=" + cookie);
    }

    public void removeCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        testUtil.doPost ("/setCookie?cookieName=" + cookie);
        testUtil.doPost ("/removeCookie?cookieName=" + cookie);
        testUtil.doPost ("/assertNoCookies");
    }
}
