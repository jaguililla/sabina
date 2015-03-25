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

package sabina.server;

import static io.undertow.Handlers.predicate;
import static io.undertow.Handlers.resource;
import static io.undertow.predicate.Predicates.suffixes;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.servlet.Filter;
import javax.servlet.ServletException;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.*;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;

final class MatcherFilterInfo extends FilterInfo implements Cloneable {
    private final MatcherFilter matcherFilter;

    public MatcherFilterInfo (final String name, final MatcherFilter aMatcher) {
        super (name, aMatcher.getClass ());
        matcherFilter = aMatcher;
    }

    @Override public FilterInfo clone () {
        MatcherFilterInfo info = new MatcherFilterInfo (getName (), matcherFilter);
        info.setAsyncSupported (isAsyncSupported ());
        return info;
    }

    @Override public InstanceFactory<? extends Filter> getInstanceFactory () {
        return (InstanceFactory<MatcherFilter>)() -> new InstanceHandle<MatcherFilter> () {
            @Override public MatcherFilter getInstance () {
                return new MatcherFilter (
                    matcherFilter.routeMatcher,
                    matcherFilter.backend,
                    matcherFilter.hasOtherHandlers);
            }

            @Override public void release () {}
        };
    }
}

/**
 * TODO Change by version with two resourceManagers (better performance ?)
 */
final class ChainResourceManager implements ResourceManager {

    private List<ResourceManager> managers = new ArrayList<> ();

    ChainResourceManager (String aStaticPath, String aFilesPath) {
        if (aStaticPath != null)
            managers.add (new ClassPathResourceManager (
                ClassLoader.getSystemClassLoader (), aStaticPath));

        if (aFilesPath != null)
            managers.add (new FileResourceManager (new File (aFilesPath), 0L));
    }

    @Override public Resource getResource (String path) throws IOException {
        for (ResourceManager rm : managers) {
            Resource res = rm.getResource (path);
            if (res != null)
                return res;
        }

        return null;
    }

    @Override public boolean isResourceChangeListenerSupported () {
        for (ResourceManager rm : managers)
            if (!rm.isResourceChangeListenerSupported ())
                return false;

        return true;
    }

    @Override public void registerResourceChangeListener (
        ResourceChangeListener listener) {

        managers.forEach (manager -> manager.registerResourceChangeListener (listener));
    }

    @Override public void removeResourceChangeListener (
        ResourceChangeListener listener) {

        managers.forEach (manager -> manager.removeResourceChangeListener (listener));
    }

    @Override public void close () throws IOException {
        for (ResourceManager rm : managers)
            rm.close ();
    }
}

final class UndertowServer implements Backend {
    private final MatcherFilter filter;
    private Undertow server;

    public UndertowServer (MatcherFilter aFilter) {
        filter = aFilter;
    }

    @Override public void startUp (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesFolder, String externalFilesFolder) {

        try {
            DeploymentManager deploymentManager =
                createDeploymentManager (staticFilesFolder, externalFilesFolder);
            deploymentManager.deploy ();

            server = (keystoreFile == null)?
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
                server.stop ();
            }
        }
        catch (Exception e) {
            e.printStackTrace ();
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

        if (aStaticFilesRoute != null || aExternalFilesLocation != null)
            deployment.addInnerHandlerChainWrapper (
                handler -> predicate (suffixes (".jpg", ".png", ".css", ".html", ".js"),
                    resource (
                        new ChainResourceManager (aStaticFilesRoute, aExternalFilesLocation)),
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
            if (keystoreFile != null)
                setProperty ("javax.net.ssl.keyStore", keystoreFile);
            if (keystorePassword != null)
                setProperty ("javax.net.ssl.keyStorePassword", keystorePassword);
            if (truststoreFile != null)
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
