package spark.webserver;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static java.lang.System.exit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;

class HiFilter implements Filter {
    @Override public void init (FilterConfig filterConfig) throws ServletException {
        // Empty
    }

    @Override public void doFilter (
        ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        PrintWriter writer = response.getWriter ();
        writer.write ("Hi!");
        writer.close ();
    }

    @Override public void destroy () {
        // Empty
    }
}

class UndertowServer implements SparkServer {

    public static void main (String[] aArgs) throws ServletException {
        new UndertowServer ().t1 ();
    }

    private final MatcherFilter filter;
    private Undertow server;

    public UndertowServer () { filter = null; }

    public UndertowServer (MatcherFilter aFilter) {
        filter = aFilter;
    }

    @Override public void ignite (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesRoute, String externalFilesLocation) {

        server = server (port, host, null);
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

    Undertow server (int aPort, String aHost, HttpHandler aHandler) {
        return Undertow.builder ()
            .addHttpListener (aPort, aHost)
            .setHandler (aHandler)
            .build ();
    }

    void t1 () throws ServletException {
        DeploymentManager manager =
            defaultContainer ().addDeployment (
                deployment ()
                    .setClassLoader (UndertowServer.class.getClassLoader ())
                    .setDeploymentName ("")
                    .setContextPath ("")
                    .addFilter (new FilterInfo ("f", HiFilter.class)));

        manager.deploy ();
        server (8080, "localhost", manager.start ()).start ();
    }
}
