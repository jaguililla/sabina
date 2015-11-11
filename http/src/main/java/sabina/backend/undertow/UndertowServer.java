/*
 * Copyright © 2014 Juan José Aguililla. All rights reserved.
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

package sabina.backend.undertow;

import static io.undertow.Handlers.predicate;
import static io.undertow.Handlers.resource;
import static io.undertow.predicate.Predicates.suffixes;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static javax.servlet.DispatcherType.REQUEST;
import static sabina.util.Strings.isEmpty;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.*;
import sabina.backend.Backend;
import sabina.servlet.MatcherFilter;

public final class UndertowServer implements Backend {
    private final MatcherFilter filter;
    private Undertow server;
    private DeploymentManager deploymentManager;

    public UndertowServer (MatcherFilter aFilter) {
        filter = aFilter;
    }

    @Override public void startUp (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesFolder, String externalFilesFolder) {

        try {
            deploymentManager = createDeploymentManager (staticFilesFolder, externalFilesFolder);
            deploymentManager.deploy ();

            server = isEmpty (keystoreFile)?
                server (port, host, deploymentManager.start ()) :
                server (port, host, deploymentManager.start (),
                    createSecureSocketContext (
                        keystoreFile, keystorePassword, truststoreFile, truststorePassword));

            server.start ();
        }
        // Wrap checked exception
        catch (ServletException e) {
            throw new RuntimeException (e);
        }
    }

    @Override public void shutDown () {
        try {
            if (server != null) {
                deploymentManager.stop ();
                deploymentManager = null;
                server.stop ();
                server = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace (); // TODO Use log
            exit (100);
        }
    }

    DeploymentManager createDeploymentManager (
        String aStaticFilesRoute, String aExternalFilesLocation) throws ServletException {

        final DeploymentInfo deployment = deployment ()
            .setClassLoader (getSystemClassLoader ())
            .setDeploymentName ("")
            .setContextPath ("")
            .addFilter (new MatcherFilterInfo ("router", filter))
            .addFilterUrlMapping ("router", "/*", REQUEST);

        /*
         * TODO Fix this, search for resource if no route match
         */
        if (!isEmpty (aStaticFilesRoute) || !isEmpty (aExternalFilesLocation))
            deployment.addInnerHandlerChainWrapper (
                handler -> predicate (suffixes (".jpg", ".png", ".css", ".html", ".js", ".svg"),
                    resource (new ChainResourceManager (aStaticFilesRoute, aExternalFilesLocation)),
                    handler
                )
            );

        return defaultContainer ().addDeployment (deployment);
    }

    Undertow server (int aPort, String aHost, HttpHandler aHandler) {
        return Undertow.builder ()
            .addHttpListener (aPort, aHost)
            .setHandler (aHandler)
            .build ();
    }

    Undertow server (int aPort, String aHost, HttpHandler aHandler, SSLContext aSSLContext) {
        return Undertow.builder ()
            .addHttpsListener (aPort, aHost, aSSLContext)
            .setHandler (aHandler)
            .build ();
    }

    private static SSLContext createSecureSocketContext (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        try {
            if (!isEmpty (keystoreFile))
                setProperty ("javax.net.ssl.keyStore", keystoreFile);
            if (keystorePassword != null)
                setProperty ("javax.net.ssl.keyStorePassword", keystorePassword);
            if (!isEmpty (truststoreFile))
                setProperty ("javax.net.ssl.trustStore", truststoreFile);
            if (truststorePassword != null)
                setProperty ("javax.net.ssl.trustStorePassword", truststorePassword);
            return SSLContext.getDefault ();
        }
        catch (NoSuchAlgorithmException e) {
            exit (-100);
            return null; // TODO Review this behaviour
        }
    }
}
