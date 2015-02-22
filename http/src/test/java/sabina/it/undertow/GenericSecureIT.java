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

import static java.lang.System.out;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static sabina.Sabina.*;
import static sabina.util.TestUtil.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.util.TestUtil;

@Test public class GenericSecureIT {

    private static TestUtil testUtil = new TestUtil ();

    @AfterClass public static void cleanup () {
        stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void setup () throws InterruptedException {
        setBackend ("undertow");

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/hi", it -> "Hello World!");

        get ("/:param", it -> "echo: " + it.params (":param"));

        get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.body ();
            it.status (201); // created
            return "Body was: " + body;
        });

        patch ("/patcher", it -> {
            String body = it.body ();
            it.status (200);
            return "Body was: " + body;
        });

        after ("/hi", it -> it.header ("after", "foobar"));

        // note that the keystore stuff is retrieved from TestUtil which
        // respects JVM params for keystore, password
        // but offers a default included store if not.
        secure (getKeyStoreLocation (), getKeystorePassword (), null, null);
        start (testUtil.getPort ());

        testUtil.waitForStartup ();
        resetBackend ();
    }

    public void getHi () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    public void hiHead () {
        UrlResponse response = testUtil.doMethodSecure ("HEAD", "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    public void getHiAfterFilter () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    public void getRoot () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    public void echoParam1 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    public void echoParam2 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    public void echoParamWithMaj () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/paramwithmaj/plop");
        assertEquals (200, response.status);
        assertEquals ("echo: plop", response.body);
    }

    public void unauthorized () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/protected/resource");
        assertTrue (urlResponse.status == 401);
    }

    @Test (enabled = false) public void notFound () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/no/resource");
        assertTrue (urlResponse.status == 404);
    }

    public void postOk () {
        UrlResponse response = testUtil.doMethodSecure ("POST", "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    public void patchOk () {
        UrlResponse response = testUtil.doMethodSecure ("PATCH", "/patcher", "Fo shizzy");
        out.println (response.body);
        assertEquals (200, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }
}
