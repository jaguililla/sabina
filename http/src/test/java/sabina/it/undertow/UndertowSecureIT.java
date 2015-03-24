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
import static sabina.util.TestUtil.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.it.Secure;
import sabina.util.TestUtil;

@Test public class UndertowSecureIT {

    private static TestUtil testUtil = new TestUtil ();

    @AfterClass public static void cleanup () {
        stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void setup () {
        setBackend ("undertow");

        Secure.setup ();

        start (testUtil.getPort ());
        testUtil.waitForStartup ();
        resetBackend ();
    }

    public void getHi () { Secure.getHi (); }
    public void hiHead () { Secure.hiHead (); }
    public void getHiAfterFilter () { Secure.getHiAfterFilter (); }
    public void getRoot () { Secure.getRoot (); }
    public void echoParam1 () { Secure.echoParam1 (); }
    public void echoParam2 () { Secure.echoParam2 (); }
    public void echoParamWithMaj () { Secure.echoParamWithMaj (); }
    public void unauthorized () { Secure.unauthorized (); }
    public void notFound () { Secure.notFound (); }
    public void postOk () { Secure.postOk (); }
    public void patchOk () { Secure.patchOk (); }
}
