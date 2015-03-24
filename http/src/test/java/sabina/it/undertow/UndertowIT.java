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
import static sabina.it.Books.testUtil;
import static sabina.util.TestUtil.*;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.it.Books;
import sabina.it.Cookies;
import sabina.it.Generic;
import sabina.it.Session;

@Test public class UndertowIT {
    @BeforeClass public static void setup () throws IOException {
        setBackend ("undertow");

        Books.setup ();
        Cookies.setup ();
        Generic.setupFile ();
        Generic.setup ();
        Session.setup ();

        start (testUtil.getPort ());
        testUtil.waitForStartup ();
        resetBackend ();
    }

    @AfterClass public static void cleanup () {
        stop ();
        testUtil.waitForShutdown ();
        Generic.cleanupFile ();
    }

    public void createBook () { Books.createBook (); }
    public void listBooks () { Books.listBooks (); }
    public void getBook () { Books.getBook (); }
    public void updateBook () { Books.updateBook (); }
    public void deleteBook () { Books.deleteBook (); }
    public void bookNotFound () { Books.bookNotFound (); }

    public void emptyCookies () { Cookies.emptyCookies (); }
    public void createCookie () { Cookies.createCookie (); }
    public void removeCookie () { Cookies.removeCookie (); }

    public void filtersShouldBeAcceptTypeAware () { Generic.filtersShouldBeAcceptTypeAware (); }
    public void routesShouldBeAcceptTypeAware () { Generic.routesShouldBeAcceptTypeAware (); }
    public void getHi () { Generic.getHi (); }
    public void hiHead () { Generic.hiHead (); }
    public void getHiAfterFilter () { Generic.getHiAfterFilter (); }
    public void getRoot () { Generic.getRoot (); }
    public void paramAndWild () { Generic.paramAndWild (); }
    public void echoParam1 () { Generic.echoParam1 (); }
    public void echoParam2 () { Generic.echoParam2 (); }
    public void echoParamWithUpperCaseInValue () { Generic.echoParamWithUpperCaseInValue (); }
    public void twoRoutesWithDifferentCase () { Generic.twoRoutesWithDifferentCase (); }
    public void echoParamWithMaj () { Generic.echoParamWithMaj (); }
    public void unauthorized () { Generic.unauthorized (); }
    public void notFound () { Generic.notFound (); }
	public void fileNotFound () { Generic.fileNotFound (); }
    public void postOk () { Generic.postOk (); }
    public void patchOk () { Generic.patchOk (); }
    public void staticFile () { Generic.staticFile (); }
    public void externalStaticFile () { Generic.externalStaticFile (); }
    public void requestData () { Generic.requestData (); }
    public void handleException () { Generic.handleException (); }
    public void methods () { Generic.methods (); }

    public void attribute () { Session.attribute (); }
}
