package sabina;

import sabina.route.RouteMatcher;

public abstract class Application implements Router {
    Server server = new Server ();

    @Override public RouteMatcher getMatcher () {
        return server.getMatcher ();
    }

    public boolean isRunning () {
        return server.isRunning ();
    }

    public void start () {
        server.start ();
    }

    /**
     * Stops the Sabina server and clears all routes
     */
    public void stop () {
        server.stop ();
    }

    /**
     * Set the IP address that Sabina should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ip.
     */
    public void bind (String ipAddress) {
        server.bind (ipAddress);
    }

    /**
     * Sets the backend used by this server. After start it would throw an exception.
     *
     * @param backend .
     */
    public void backend (String backend) {
        server.backend (backend);
    }

    /**
     * Set the port that Sabina should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public void port (int port) {
        server.port (port);
    }

    public void port (String port) {
        server.port (port);
    }

    public void start (String bind, int port) {
        server.start (bind, port);
    }

    public void start (String bind, String port) {
        server.start (bind, port);
    }

    public void start (int port) {
        server.start (port);
    }

    public void start (String port) {
        server.start (port);
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public void filesLocation (String externalFolder) {
        server.filesLocation (externalFolder);
    }

    public void filesLocation (String folder, String externalFolder) {
        server.filesLocation (folder, externalFolder);
    }

    public void secure (String keystoreFile, String keystorePassword) {
        server.secure (keystoreFile, keystorePassword);
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * <p>
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *  @param keystoreFile The keystore file location as string
     * @param keystorePassword the password for the keystore
     * @param truststoreFile the truststore file location as string, leave null to reuse
     * keystore
     * @param truststorePassword the trust store password
     */
    public void secure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        server.secure (keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    /**
     * Sets the folder in classp serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param folder the folder in classp.
     */
    public void resourcesLocation (String folder) {
        server.resourcesLocation (folder);
    }
}
