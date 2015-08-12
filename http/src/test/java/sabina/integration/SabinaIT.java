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

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static sabina.integration.TestScenario.getKeyStoreLocation;
import static sabina.integration.TestScenario.getKeystorePassword;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sabina.Server;

/**
 * The goal is to have a single IT running all scenarios in different servers and different ports.
 * Then each test is executed agains all started servers.
 *
 * This could seem tricky (because it is), if you know a more straighforward way to do this, please,
 * please... mail me.
 *
 * @author jam
 */
@Test public class SabinaIT {
    private static List<TestScenario> scenarios = asList (
        new TestScenario ("undertow", 6011, false, false),
        new TestScenario ("undertow", 6012, false, true),
        new TestScenario ("undertow", 6013, true, false),
        new TestScenario ("undertow", 6014, true, true),
        new TestScenario ("jetty", 6021, false, false),
        new TestScenario ("jetty", 6022, false, true),
        new TestScenario ("jetty", 6023, true, false),
        new TestScenario ("jetty", 6024, true, true)
    );

    private static File tmpExternalFile;
    private static List<Server> servers = new ArrayList<> ();

    @BeforeClass public static void disableLogging () {
        Logger rootLogger = getLogger ("");
        Stream.of (rootLogger.getHandlers ()).forEach (it -> it.setLevel (INFO));
        rootLogger.setLevel (INFO);
    }

    @BeforeClass public static void setupFile () throws IOException {
        tmpExternalFile = new File (getProperty ("java.io.tmpdir"), "externalFile.html");

        try (FileWriter writer = new FileWriter (tmpExternalFile)) {
            writer.write ("Content of external file");
            writer.flush ();
        }
    }

    @BeforeClass public static void setup () throws IOException {
        for (TestScenario tu : scenarios) {
            Server s = new Server (tu.backend, tu.port);
            servers.add (s);

            Books.setup (s);
            Cookies.setup (s);
            Generic.setup (s);
            Session.setup (s);
            Routes.setup (s);

            if (tu.secure)
                s.secure (getKeyStoreLocation (), getKeystorePassword ());
            if (tu.externalFiles)
                s.filesLocation ("/public", getProperty ("java.io.tmpdir"));

            s.start ();
        }
        scenarios.stream ().forEach (TestScenario::waitForStartup);
    }

    @AfterClass public static void cleanup () {
        servers.stream ().forEach (Server::stop);
        scenarios.stream ().forEach (TestScenario::waitForShutdown);
    }

    @AfterClass public static void cleanupFile () {
        if (tmpExternalFile != null)
            if (!tmpExternalFile.delete ())
                throw new IllegalStateException ();
    }

    @DataProvider (name = "scenarios")
    public Object[][] scenarios () {
        Object [][] result = new Object [scenarios.size ()][1];

        int ii = 0;
        for (TestScenario tu : scenarios) {
            result[ii++] = new Object [] { tu };
        }

        return result;
    }

    @Test(dataProvider = "scenarios")
    public void createBook (TestScenario testScenario) { Books.createBook (testScenario); }
    @Test(dataProvider = "scenarios")
    public void listBooks (TestScenario testScenario) { Books.listBooks (testScenario); }
    @Test(dataProvider = "scenarios")
    public void getBook (TestScenario testScenario) { Books.getBook (testScenario); }
    @Test(dataProvider = "scenarios")
    public void updateBook (TestScenario testScenario) { Books.updateBook (testScenario); }
    @Test(dataProvider = "scenarios")
    public void deleteBook (TestScenario testScenario) { Books.deleteBook (testScenario); }
    @Test(dataProvider = "scenarios")
    public void bookNotFound (TestScenario testScenario) { Books.bookNotFound (testScenario); }

    @Test(dataProvider = "scenarios")
    public void emptyCookies (TestScenario testScenario) { Cookies.emptyCookies (testScenario); }
    @Test(dataProvider = "scenarios")
    public void createCookie (TestScenario testScenario) { Cookies.createCookie (testScenario); }
    @Test(dataProvider = "scenarios")
    public void removeCookie (TestScenario testScenario) { Cookies.removeCookie (testScenario); }

    @Test(dataProvider = "scenarios")
    public void filtersShouldBeAcceptTypeAware (TestScenario testScenario) {
        Generic.filtersShouldBeAcceptTypeAware (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void routesShouldBeAcceptTypeAware (TestScenario testScenario) {
        Generic.routesShouldBeAcceptTypeAware (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void getHi (TestScenario testScenario) { Generic.getHi (testScenario); }
    @Test(dataProvider = "scenarios")
    public void hiHead (TestScenario testScenario) { Generic.hiHead (testScenario); }
    @Test(dataProvider = "scenarios")
    public void getHiAfterFilter (TestScenario testScenario) {
        Generic.getHiAfterFilter (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void getRoot (TestScenario testScenario) { Generic.getRoot (testScenario); }
    @Test(dataProvider = "scenarios")
    public void paramAndWild (TestScenario testScenario) { Generic.paramAndWild (testScenario); }
    @Test(dataProvider = "scenarios")
    public void echoParam1 (TestScenario testScenario) { Generic.echoParam1 (testScenario); }
    @Test(dataProvider = "scenarios")
    public void echoParam2 (TestScenario testScenario) { Generic.echoParam2 (testScenario); }
    @Test(dataProvider = "scenarios")
    public void echoParamWithUpperCaseInValue (TestScenario testScenario) {
        Generic.echoParamWithUpperCaseInValue (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void twoRoutesWithDifferentCase (TestScenario testScenario) {
        Generic.twoRoutesWithDifferentCase (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void echoParamWithMaj (TestScenario testScenario) { Generic.echoParamWithMaj (
        testScenario); }
    @Test(dataProvider = "scenarios")
    public void unauthorized (TestScenario testScenario) { Generic.unauthorized (testScenario); }
    @Test(dataProvider = "scenarios")
    public void notFound (TestScenario testScenario) { Generic.notFound (testScenario); }
    @Test(dataProvider = "scenarios")
    public void fileNotFound (TestScenario testScenario) { Generic.fileNotFound (testScenario); }
    @Test(dataProvider = "scenarios")
    public void postOk (TestScenario testScenario) { Generic.postOk (testScenario); }
    @Test(dataProvider = "scenarios")
    public void patchOk (TestScenario testScenario) { Generic.patchOk (testScenario); }
    @Test(dataProvider = "scenarios")
    public void staticFile (TestScenario testScenario) { Generic.staticFile (testScenario); }
    @Test(dataProvider = "scenarios")
    public void externalStaticFile (TestScenario testScenario) {
        Generic.externalStaticFile (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void halt (TestScenario testScenario) { Generic.halt (testScenario); }
    @Test(dataProvider = "scenarios")
    public void requestData (TestScenario testScenario) { Generic.requestData (testScenario); }
    @Test(dataProvider = "scenarios")
    public void handleException (TestScenario testScenario) {
        Generic.handleException (testScenario);
    }
    @Test(dataProvider = "scenarios")
    public void reqres (TestScenario testScenario) { Generic.reqres (testScenario); }

    @Test(dataProvider = "scenarios")
    public void methods (TestScenario testScenario) { Routes.methods (testScenario); }

    @Test(dataProvider = "scenarios")
    public void attribute (TestScenario testScenario) { Session.attribute (testScenario); }
}
