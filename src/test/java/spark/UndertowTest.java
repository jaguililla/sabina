package spark;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

public class UndertowTest {
    static void server (int aPort, String aHost, HttpHandler aHandler) {
        Undertow.builder()
            .addHttpListener(aPort, aHost)
            .setHandler (aHandler)
            .build()
            .start ();
    }

    public static void main (String [] aArgs) {
        server (8080, "localhost", it -> {
            it.getResponseHeaders ().put (Headers.CONTENT_TYPE, "text/plain");
            it.getResponseSender ().send ("Hello World");
        });
    }
}
