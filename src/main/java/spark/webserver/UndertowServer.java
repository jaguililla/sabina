package spark.webserver;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;

class MessageServlet extends HttpServlet {
    public static final String MESSAGE = "message";

    private String message;

    @Override public void init (final ServletConfig config) throws ServletException {
        super.init (config);
        message = config.getInitParameter (MESSAGE);
    }

    @Override
    protected void doGet (final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {

        PrintWriter writer = resp.getWriter ();
        writer.write (message);
        writer.close ();
    }

    @Override
    protected void doPost (final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {

        doGet (req, resp);
    }
}

class UndertowServer implements SparkServer {

    public static void main (String[] aArgs) throws ServletException {
        new UndertowServer ().t1 ();
    }

    @Override public void ignite (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesRoute, String externalFilesLocation) {

    }

    @Override public void stop () {

    }

    void server (int aPort, String aHost, HttpHandler aHandler) {
        Undertow.builder ()
            .addHttpListener (aPort, aHost)
            .setHandler (aHandler)
            .build ()
            .start ();
    }

    void t0 () {
        server (8080, "localhost", it -> {
            it.getResponseHeaders ().put (Headers.CONTENT_TYPE, "text/plain");
            it.getResponseSender ().send ("Hello World");
        });
    }

    void t1 () throws ServletException {
        DeploymentInfo servletBuilder = Servlets.deployment ()
            .setClassLoader (UndertowServer.class.getClassLoader ())
            .addServlets (
                Servlets.servlet ("MessageServlet", MessageServlet.class)
                    .addInitParam ("message", "Hello World")
                    .addMapping ("/*"),
                Servlets.servlet ("MyServlet", MessageServlet.class)
                    .addInitParam ("message", "MyServlet")
                    .addMapping ("/myservlet"));

        DeploymentManager manager =
            Servlets.defaultContainer ().addDeployment (servletBuilder);
        manager.deploy ();
        PathHandler path = Handlers.path (Handlers.redirect ("/myapp"))
            .addPrefixPath ("/myapp", manager.start ());

        Undertow server = Undertow.builder ()
            .addHttpListener (8080, "localhost")
            .setHandler (path)
            .build ();
        server.start ();
    }
}
