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

package sabina.server;

import static java.lang.System.exit;
import static co.there4.bali.Strings.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Simple Jetty Handler.
 *
 * @author Per Wendel
 */
final class JettyHandler extends SessionHandler {
    private MatcherFilter filter;

    public JettyHandler (MatcherFilter filter) {
        this.filter = filter;
    }

    @Override public void doHandle (
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {

        filter.doFilter (request, response, null);
        baseRequest.setHandled (filter.handled);
    }
}

/**
 * Sabina server backend implementation
 *
 * @author Per Wendel
 */
final class JettyServer implements Backend {

    private static final String NAME = "Sabina";
    private Handler handler;
    private Server server;

    public JettyServer (MatcherFilter handler) {
        this.handler = new JettyHandler (handler);
    }

    public void startUp (
        String host, int port, String keystoreFile,
        String keystorePassword, String truststoreFile,
        String truststorePassword, String staticFilesFolder,
        String externalFilesFolder) {

        ServerConnector connector;

        if (isEmpty (keystoreFile)) {
            connector = createSocketConnector ();
        }
        else {
            connector = createSecureSocketConnector (keystoreFile,
                keystorePassword, truststoreFile, truststorePassword);
        }

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout (TimeUnit.HOURS.toMillis (1));
        connector.setSoLingerTime (-1);
        connector.setHost (host);
        connector.setPort (port);

        server = connector.getServer ();
        server.setConnectors (new Connector[] { connector });

        // Handle static file routes
        if (isEmpty (staticFilesFolder) && isEmpty(externalFilesFolder)) {
            server.setHandler (handler);
        }
        else {
            List<Handler> handlersInList = new ArrayList<> ();
            handlersInList.add (handler);

            // Set static file location
            setStaticFileLocationIfPresent (staticFilesFolder, handlersInList);

            // Set external static file location
            setExternalStaticFileLocationIfPresent (externalFilesFolder, handlersInList);

            HandlerList handlers = new HandlerList ();
            handlers
                .setHandlers (handlersInList.toArray (new Handler[handlersInList.size ()]));
            server.setHandler (handlers);
        }

        try {
            System.out.println ("=== " + NAME + " has ignited ..."); // TODO Use logger
            System.out.println (">>> Listening on " + host + ":" + port);

            server.start ();
            server.join ();
        }
        catch (Exception e) {
            e.printStackTrace ();
            exit (100);
        }
    }

    public void shutDown () {
        System.out.print (">>> " + NAME + " shutting down...");
        try {
            if (server != null)
                server.stop ();
        }
        catch (Exception e) {
            e.printStackTrace (); // TODO Use logger
            exit (100);
        }
        System.out.println ("done");
    }

    /**
     * Creates a secure jetty socket connector. Keystore required, truststore
     * optional. If truststore not specifed keystore will be reused.
     *
     * @param keystoreFile The keystore file location as string
     * @param keystorePassword the password for the keystore
     * @param truststoreFile the truststore file location as string, leave null to reuse
     * keystore
     * @param truststorePassword the trust store password
     *
     * @return a secure socket connector
     */
    private static ServerConnector createSecureSocketConnector (
        String keystoreFile,
        String keystorePassword, String truststoreFile,
        String truststorePassword) {

        SslContextFactory sslContextFactory = new SslContextFactory (keystoreFile);

        if (keystorePassword != null) {
            sslContextFactory.setKeyStorePassword (keystorePassword);
        }
        if (!isEmpty (truststoreFile)) {
            sslContextFactory.setTrustStorePath (truststoreFile);
        }
        if (truststorePassword != null) {
            sslContextFactory.setTrustStorePassword (truststorePassword);
        }
        return new ServerConnector (new Server (), sslContextFactory);
    }

    /**
     * Creates an ordinary, non-secured Jetty server connector.
     *
     * @return - a server connector
     */
    private static ServerConnector createSocketConnector () {
        return new ServerConnector (new Server ());
    }

    /**
     * Sets static file location if present
     */
    private static void setStaticFileLocationIfPresent (
        String staticFilesRoute, List<Handler> handlersInList) {
        if (staticFilesRoute != null) {
            ResourceHandler resourceHandler = new ResourceHandler ();
            Resource staticResources = Resource.newClassPathResource (staticFilesRoute);
            resourceHandler.setBaseResource (staticResources);
            resourceHandler.setWelcomeFiles (new String[] { "index.html" });
            handlersInList.add (resourceHandler);
        }
    }

    /**
     * Sets external static file location if present
     */
    private static void setExternalStaticFileLocationIfPresent (
        String externalFilesRoute, List<Handler> handlersInList) {
        if (externalFilesRoute != null) {
            ResourceHandler externalResourceHandler = new ResourceHandler ();
            Resource externalStaticResources =
                Resource.newResource (new File (externalFilesRoute));
            externalResourceHandler.setBaseResource (externalStaticResources);
            externalResourceHandler.setWelcomeFiles (new String[] { "index.html" });
            handlersInList.add (externalResourceHandler);
        }
    }
}
