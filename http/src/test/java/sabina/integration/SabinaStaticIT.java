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
import static java.util.logging.Level.FINEST;
import static java.util.logging.Logger.getLogger;
import static sabina.Sabina.*;
import static sabina.integration.TestScenario.*;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.Route.VoidHandler;

@Test public class SabinaStaticIT {
    private static TestScenario testScenario = new TestScenario ("undertow", 4567, true, true);
    private static String part = "param";

    @BeforeClass public static void setupLogging () {
        Logger rootLogger = getLogger ("");
        Stream.of (rootLogger.getHandlers ()).forEach (it -> it.setLevel (FINEST));
        rootLogger.setLevel (FINEST);
    }

    @BeforeClass public static void setupFile () throws IOException { SabinaIT.setupFile (); }

    @BeforeClass public static void setup () {
        get ("/reset/route", it -> "should not be executed");

        reset ();

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        before ("/protected/*", "application/json", it ->
            it.halt (401, "{\"message\": \"Go Away!\"}")
        );

        get ("/request/data", it -> {
            it.response.body (it.url ());

            it.cookie ("method", it.requestMethod ());
            it.cookie ("host", it.ip ());
            it.cookie ("uri", it.uri ());
            it.cookie ("params", String.valueOf (it.params ().size ()));

            it.header ("agent", it.userAgent ());
            it.header ("protocol", it.protocol ());
            it.header ("scheme", it.scheme ());
            it.header ("host", it.host ());
            it.header ("query", it.queryString ());
            it.header ("port", String.valueOf (it.port ()));

            return it.response.body () + "!!!";
        });

        get ("/error500", (VoidHandler)it -> { throw new IllegalStateException (); });

        exception (
            UnsupportedOperationException.class,
            (ex, req) -> req.response.header ("error", ex.getMessage ())
        );

        get ("/exception", (VoidHandler)it -> {
            throw new UnsupportedOperationException ("error message");
        });

        get ("/hi", "application/json", it -> "{\"message\": \"Hello World\"}");

        get ("/hi", it -> "Hello World!");

        get ("/param/:param", it -> "echo: " + it.params (":param"));

        get ("/paramandwild/:param/stuff/*", it ->
                "paramandwild: " + it.params (":param") + it.splat ().get (0)
        );

        get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.body ();
            it.response.status (201); // created
            return "Body was: " + body;
        });

        patch ("/patcher", it -> {
            String body = it.body ();
            it.response.status (200);
            return "Body was: " + body;
        });

        // Test all route methods
        delete ("/routes/method", Routes::methodHandler);
        options ("/routes/method", Routes::methodHandler);
        get ("/routes/method", Routes::methodHandler);
        patch ("/routes/method", Routes::methodHandler);
        post ("/routes/method", Routes::methodHandler);
        put ("/routes/method", Routes::methodHandler);
        trace ("/routes/method", Routes::methodHandler);
        head ("/routes/method", Routes::methodHandler);

        delete ("/routes/method", Routes::voidMethodHandler);
        options ("/routes/method", Routes::voidMethodHandler);
        get ("/routes/method", Routes::voidMethodHandler);
        patch ("/routes/method", Routes::voidMethodHandler);
        post ("/routes/method", Routes::voidMethodHandler);
        put ("/routes/method", Routes::voidMethodHandler);
        trace ("/routes/method", Routes::voidMethodHandler);
        head ("/routes/method", Routes::voidMethodHandler);

        delete ("/routes/method", "application/json", Routes::ctMethodHandler);
        options ("/routes/method", "application/json", Routes::ctMethodHandler);
        get ("/routes/method", "application/json", Routes::ctMethodHandler);
        patch ("/routes/method", "application/json", Routes::ctMethodHandler);
        post ("/routes/method", "application/json", Routes::ctMethodHandler);
        put ("/routes/method", "application/json", Routes::ctMethodHandler);
        trace ("/routes/method", "application/json", Routes::ctMethodHandler);
        head ("/routes/method", "application/json", Routes::ctMethodHandler);

        delete ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        options ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        get ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        patch ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        post ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        put ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        trace ("/routes/method", "application/json", Routes::ctVoidMethodHandler);
        head ("/routes/method", "application/json", Routes::ctVoidMethodHandler);

        get ("/halt", it -> {
            it.halt (500, "halted");
        });

        get ("/tworoutes/" + part + "/:param", it ->
                part + " route: " + it.params (":param")
        );

        get ("/tworoutes/" + part.toUpperCase () + "/:param", it ->
                part.toUpperCase () + " route: " + it.params (":param")
        );

        after ("/hi", it -> it.response.header ("after", "foobar"));

        secure (getKeyStoreLocation (), getKeystorePassword ());
        filesLocation ("/public", getProperty ("java.io.tmpdir"));

        start (testScenario.port);
        testScenario.waitForStartup ();
    }

    @AfterClass public static void cleanup () {
        stop ();
        testScenario.waitForShutdown ();
    }

    @AfterClass public static void cleanupFile () { SabinaIT.cleanupFile (); }

    public void filtersShouldBeAcceptTypeAware () {
        Generic.filtersShouldBeAcceptTypeAware (testScenario);
    }

    public void routesShouldBeAcceptTypeAware () {
        Generic.routesShouldBeAcceptTypeAware (testScenario);
    }

    public void echoParamWithUpperCaseInValue () {
        Generic.echoParamWithUpperCaseInValue (testScenario);
    }

    public void twoRoutesWithDifferentCase () { Generic.twoRoutesWithDifferentCase (testScenario); }
    public void getHi () { Generic.getHi (testScenario); }
    public void hiHead () { Generic.hiHead (testScenario); }
    public void getHiAfterFilter () { Generic.getHiAfterFilter (testScenario); }
    public void getRoot () { Generic.getRoot (testScenario); }
    public void paramAndWild () { Generic.paramAndWild (testScenario); }
    public void echoParam1 () { Generic.echoParam1 (testScenario); }
    public void echoParam2 () { Generic.echoParam2 (testScenario); }
    public void echoParamWithMaj () { Generic.echoParamWithMaj (testScenario); }
    public void unauthorized () { Generic.unauthorized (testScenario); }
    public void notFound () { Generic.notFound (testScenario); }
	public void fileNotFound () { Generic.fileNotFound (testScenario); }
    public void postOk () { Generic.postOk (testScenario); }
    public void patchOk () { Generic.patchOk (testScenario); }
    public void staticFile () { Generic.staticFile (testScenario); }
    public void externalStaticFile () { Generic.externalStaticFile (testScenario); }
    public void halt () { Generic.halt (testScenario); }
    public void requestData () { Generic.requestData (testScenario); }
    public void handleException () { Generic.handleException (testScenario); }
    public void methods () { Routes.methods (testScenario); }

    public void routes_after_reset_are_not_available () {
        UrlResponse response = testScenario.doGet ("/reset/route");
        assert response.status == 404;
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void reset_on_a_running_server_raises_an_error () {
        reset ();
    }

    public void uncaugh_exception_return_a_500_error () {
        UrlResponse response = testScenario.doGet ("/error500");
        assert response.body.equals ("<html><body><h2>500 Internal Error</h2></body></html>");
        assert response.status == 500;
    }
}
