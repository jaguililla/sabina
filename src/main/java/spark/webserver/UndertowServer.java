package spark.webserver;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static java.lang.System.exit;
import static javax.servlet.DispatcherType.REQUEST;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentManager;
import org.eclipse.jetty.util.ssl.SslContextFactory;

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
        String staticFilesRoute, String externalFilesLocation) throws ServletException {

        DeploymentManager deploymentManager = createDeploymentManager ();
        deploymentManager.deploy ();

        server = (keystoreFile == null)?
            server (8080, "localhost", deploymentManager.start ()) :
            server (8080, "localhost", deploymentManager.start (),
                createSecureSocketContext (
                    keystoreFile, keystorePassword, truststoreFile, truststorePassword));

        server.start ();
    }

    @Override public void stop () {
        try {
            if (server != null)
                server.stop ();
        }
        catch (Exception e) {
            e.printStackTrace ();
            exit (100);
        }
    }

    Undertow server (int aPort, String aHost, HttpHandler aHandler, SSLContext aSSLContext) {
        return Undertow.builder ()
            .addHttpsListener (aPort, aHost, aSSLContext)
            .setHandler (aHandler)
            .build ();
    }

    Undertow server (int aPort, String aHost, HttpHandler aHandler) {
        return Undertow.builder ()
            .addHttpListener (aPort, aHost)
            .setHandler (aHandler)
            .build ();
    }

    private static SSLContext createSecureSocketContext (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        SslContextFactory sslContextFactory = new SslContextFactory (keystoreFile);

        if (keystorePassword != null)
            sslContextFactory.setKeyStorePassword (keystorePassword);

        if (truststoreFile != null)
            sslContextFactory.setTrustStorePath (truststoreFile);

        if (truststorePassword != null)
            sslContextFactory.setTrustStorePassword (truststorePassword);

        return sslContextFactory.getSslContext ();
    }

    DeploymentManager createDeploymentManager () throws ServletException {
        return defaultContainer ().addDeployment (
            deployment ()
                .setClassLoader (UndertowServer.class.getClassLoader ())
                .setDeploymentName ("")
                .setContextPath ("")
                .addFilter (Servlets.filter ("f", filter.getClass ())) // TODO This WON'T work
                .addFilterUrlMapping ("f", "/*", REQUEST));
    }
}
