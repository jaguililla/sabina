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

import static java.lang.System.out;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static sabina.Sabina.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.util.TestUtil;

public class GenericSecureIntegrationTest {

    private static TestUtil testUtil = new TestUtil (4560);

    @AfterClass public static void shutDown () {
        stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void startUp () throws InterruptedException {
        setPort (testUtil.getPort ());

        // note that the keystore stuff is retrieved from SparkTestUtil which
        // respects JVM params for keystore, password
        // but offers a default included store if not.
        Sabina.setSecure (
            TestUtil.getKeyStoreLocation (), TestUtil.getKeystorePassword (), null,
            null);

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/hi", it -> "Hello World!");

        get ("/:param", it -> "echo: " + it.params (":param"));

        get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        patch ("/patcher", it -> {
            String body = it.requestBody ();
            it.status (200);
            return "Body was: " + body;
        });

        after ("/hi", it -> it.header ("after", "foobar"));

        testUtil.waitForStartup ();
    }

    @Test public void getHi () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void hiHead () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("HEAD", "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void getHiAfterFilter () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void getRoot () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("GET", "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void echoParam1 () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("GET", "/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void echoParam2 () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("GET", "/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void echoParamWithMaj () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("GET", "/paramwithmaj/plop");
        assertEquals (200, response.status);
        assertEquals ("echo: plop", response.body);
    }

    @Test public void unauthorized () throws Exception {
        TestUtil.UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/protected/resource");
        assertTrue (urlResponse.status == 401);
    }

    @Test public void notFound () throws Exception {
        TestUtil.UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/no/resource");
        assertTrue (urlResponse.status == 404);
    }

    @Test public void postOk () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("POST", "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void patchOk () {
        TestUtil.UrlResponse response = testUtil.doMethodSecure ("PATCH", "/patcher", "Fo shizzy");
        out.println (response.body);
        assertEquals (200, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }
}
