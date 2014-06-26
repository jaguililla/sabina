package spark.webserver;

import static io.undertow.Handlers.resource;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.servlet.Filter;
import javax.servlet.ServletException;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.*;
import io.undertow.servlet.api.*;

class MatcherFilterInfo extends FilterInfo implements Cloneable {
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
                    matcherFilter.isServletContext,
                    matcherFilter.hasOtherHandlers);
            }

            @Override public void release () {}
        };
    }
}

class UndertowServer implements SparkServer {
    private final MatcherFilter filter;
    private Undertow server;

    public UndertowServer (MatcherFilter aFilter) {
        filter = aFilter;
    }

    @Override public void ignite (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesRoute, String externalFilesLocation) {

        try {
            DeploymentManager deploymentManager =
                createDeploymentManager (staticFilesRoute, externalFilesLocation);
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

    @Override public void stop () {
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
            .setClassLoader (UndertowServer.class.getClassLoader ())
            .setDeploymentName ("")
            .setContextPath ("")
            .addFilter (new MatcherFilterInfo ("router", filter))
            .addFilterUrlMapping ("router", "/*", REQUEST);

        return (aStaticFilesRoute != null)?
//            defaultContainer ().addDeployment (deployment.addOuterHandlerChainWrapper (
//                resource (new FileResourceManager (null, 0L))) :
            defaultContainer ().addDeployment (deployment) :
            defaultContainer ().addDeployment (deployment);
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
