package spark.webserver;

public class UndertowServer implements SparkServer {

    @Override public void ignite (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesRoute, String externalFilesLocation) {

    }

    @Override public void stop () {

    }
}
