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

import static sabina.Sabina.start;
import static sabina.Sabina.stop;
import static sabina.it.Secure.testUtil;
import static sabina.util.TestUtil.resetBackend;
import static sabina.util.TestUtil.setBackend;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.it.Secure;

@Test public class JettySecureIT {
    @AfterClass public static void cleanup () {
        stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void setup () {
        setBackend ("jetty");

        Secure.setup ();

        start (testUtil.getPort ());
        testUtil.waitForStartup ();
        resetBackend ();
    }

    @Test (enabled = false) public void getHi () { Secure.getHi (); }
    @Test (enabled = false) public void hiHead () { Secure.hiHead (); }
    @Test (enabled = false) public void getHiAfterFilter () { Secure.getHiAfterFilter (); }
    @Test (enabled = false) public void getRoot () { Secure.getRoot (); }
    @Test (enabled = false) public void echoParam1 () { Secure.echoParam1 (); }
    @Test (enabled = false) public void echoParam2 () { Secure.echoParam2 (); }
    @Test (enabled = false) public void echoParamWithMaj () { Secure.echoParamWithMaj (); }
    @Test (enabled = false) public void unauthorized () { Secure.unauthorized (); }
    @Test (enabled = false) public void notFound () { Secure.notFound (); }
    @Test (enabled = false) public void postOk () { Secure.postOk (); }
    @Test (enabled = false) public void patchOk () { Secure.patchOk (); }
}
