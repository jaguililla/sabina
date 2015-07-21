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

package sabina.integration;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.System.out;
import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
import static org.testng.Assert.assertEquals;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

final class TestScenario {
    static class UrlResponse {
        public Map<String, String> headers;
        public Map<String, String> cookies;
        public String body;
        public int status;
    }

    final int port;
    final String backend;
    final boolean secure, externalFiles;

    private final HttpClient httpClient;
    private final CookieStore cookieStore;

    TestScenario (String backend, int port, boolean secure, boolean externalFiles) {
        this.port = port;
        this.backend = backend;
        this.secure = secure;
        this.externalFiles = externalFiles;

        SSLConnectionSocketFactory sslConnectionSocketFactory =
            new SSLConnectionSocketFactory (getSslFactory (), ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory>create ()
                .register ("http", PlainConnectionSocketFactory.INSTANCE)
                .register ("https", sslConnectionSocketFactory)
                .build ();

        HttpClientConnectionManager connManager =
            new BasicHttpClientConnectionManager (socketFactoryRegistry,
                new ManagedHttpClientConnectionFactory ());

        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec (CookieSpecs.NETSCAPE).build ();
        cookieStore = new BasicCookieStore ();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        this.httpClient = HttpClients.custom ()
            .setSchemePortResolver (h -> {
                Args.notNull (h, "HTTP host");
                final int port1 = h.getPort ();
                if (port1 > 0) {
                    return port1;
                }

                final String name = h.getSchemeName ();
                if (name.equalsIgnoreCase ("http")) {
                    return port1;
                }
                else if (name.equalsIgnoreCase ("https")) {
                    return port1;
                }
                else {
                    throw new UnsupportedSchemeException ("unsupported protocol: " + name);
                }
            })
            .setConnectionManager (connManager)
            .setDefaultRequestConfig (globalConfig)
            .build ();
    }

    UrlResponse doMethod (String requestMethod, String path) {
        return doMethod (requestMethod, path, null, secure, "text/html");
    }

    UrlResponse doMethod (String requestMethod, String path, String body) {
        return doMethod (requestMethod, path, body, secure, "text/html");
    }

    UrlResponse doMethod (String requestMethod, String path, String body, String acceptType) {
        return doMethod (requestMethod, path, body, secure, acceptType);
    }

    private UrlResponse doMethod (
        String method, String path, String body, boolean secure, String acceptType) {

        try {
            HttpUriRequest httpRequest = getHttpRequest (method, path, body, secure, acceptType);
            HttpResponse httpResponse = httpClient.execute (httpRequest);

            UrlResponse urlResponse = new UrlResponse ();
            urlResponse.status = httpResponse.getStatusLine ().getStatusCode ();

            HttpEntity entity = httpResponse.getEntity ();
            urlResponse.body = entity != null? EntityUtils.toString (entity) : "";

            Map<String, String> headers = new HashMap<> ();
            Header[] allHeaders = httpResponse.getAllHeaders ();
            for (Header header : allHeaders)
                headers.put (header.getName (), header.getValue ());

            urlResponse.headers = headers;
//            cookieStore.getCookies ().stream ().collect (toMap (Cookie::getName, Cookie::getValue));
            return urlResponse;
        }
        catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    UrlResponse doPost (String aPath) { return doMethod ("POST", aPath, ""); }
    UrlResponse doGet (String aPath) { return doMethod ("GET", aPath, ""); }
    UrlResponse doPut (String aPath) { return doMethod ("PUT", aPath, ""); }
    UrlResponse doDelete (String aPath) { return doMethod ("DELETE", aPath, ""); }

    private HttpUriRequest getHttpRequest (
        String method, String path, String body, boolean secure, String acceptType) {

        if (body == null)
            body = "";

        try {
            String protocol = secure? "https" : "http";
            String uri = protocol + "://localhost:" + port + path;

            if (method.equals ("GET")) {
                HttpGet httpGet = new HttpGet (uri);
                httpGet.setHeader ("Accept", acceptType);
                return httpGet;
            }

            if (method.equals ("POST")) {
                HttpPost httpPost = new HttpPost (uri);
                httpPost.setHeader ("Accept", acceptType);
                httpPost.setEntity (new StringEntity (body));
                return httpPost;
            }

            if (method.equals ("PATCH")) {
                HttpPatch httpPatch = new HttpPatch (uri);
                httpPatch.setHeader ("Accept", acceptType);
                httpPatch.setEntity (new StringEntity (body));
                return httpPatch;
            }

            if (method.equals ("DELETE")) {
                HttpDelete httpDelete = new HttpDelete (uri);
                httpDelete.setHeader ("Accept", acceptType);
                return httpDelete;
            }

            if (method.equals ("PUT")) {
                HttpPut httpPut = new HttpPut (uri);
                httpPut.setHeader ("Accept", acceptType);
                httpPut.setEntity (new StringEntity (body));
                return httpPut;
            }

            if (method.equals ("HEAD"))
                return new HttpHead (uri);

            if (method.equals ("TRACE"))
                return new HttpTrace (uri);

            if (method.equals ("OPTIONS"))
                return new HttpOptions (uri);

            throw new IllegalArgumentException ("Unknown method " + method);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Convenience method to use own truststore on SSL Sockets. Will default to
     * the self signed keystore provided in resources, but will respect
     * <p>
     * -Djavax.net.ssl.keyStore=serverKeys
     * -Djavax.net.ssl.keyStorePassword=password
     * -Djavax.net.ssl.trustStore=serverTrust
     * -Djavax.net.ssl.trustStorePassword=password SSLApplication
     * <p>
     * So these can be used to specify other key/trust stores if required.
     *
     * @return an SSL Socket Factory using either provided keystore OR the
     * keystore specified in JVM params
     */
    private SSLSocketFactory getSslFactory () {
        KeyStore keyStore;

        try {
            keyStore = KeyStore.getInstance (KeyStore.getDefaultType ());
            FileInputStream fis = new FileInputStream (getTrustStoreLocation ());
            keyStore.load (fis, getTrustStorePassword ().toCharArray ());
            fis.close ();

            TrustManagerFactory tmf =
                TrustManagerFactory.getInstance (TrustManagerFactory.getDefaultAlgorithm ());
            tmf.init (keyStore);
            SSLContext ctx = SSLContext.getInstance ("TLS");
            ctx.init (null, tmf.getTrustManagers (), null);
            return ctx.getSocketFactory ();
        }
        catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }

    /**
     * Returns the base directory for files.
     *
     * @return Base directory for files.
     */
    private static String getBase () {
        return new File ("./src").exists ()? "./src" : "./http/src";
    }

    /**
     * Return JVM param set keystore or default if not set.
     *
     * @return Keystore location as string
     */
    static String getKeyStoreLocation () {
        String keyStoreLoc = getProperty ("javax.net.ssl.keyStore");
        return keyStoreLoc == null? getBase () + "/test/resources/keystore.jks" : keyStoreLoc;
    }

    /**
     * Return JVM param set keystore password or default if not set.
     *
     * @return Keystore password as string
     */
    static String getKeystorePassword () {
        String password = getProperty ("javax.net.ssl.keyStorePassword");
        return password == null? "password" : password;
    }

    /**
     * Return JVM param set truststore location, or keystore location if not
     * set. if keystore not set either, returns default
     *
     * @return truststore location as string
     */
    static String getTrustStoreLocation () {
        String trustStoreLoc = getProperty ("javax.net.ssl.trustStore");
        return trustStoreLoc == null? getKeyStoreLocation () : trustStoreLoc;
    }

    /**
     * Return JVM param set truststore password or keystore password if not set.
     * If still not set, will return default password
     *
     * @return truststore password as string
     */
    static String getTrustStorePassword () {
        String password = getProperty ("javax.net.ssl.trustStorePassword");
        return password == null? getKeystorePassword () : password;
    }

    private static void sleep (long time) {
        try {
            Thread.sleep (time);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }

    void waitForStartup () {
        waitForStartup ("localhost", port);
    }

    private static void waitForStartup (String host, int port) {
        waitForStartup (host, port, 5, 100);
    }

    private static void waitForStartup (String host, int port, long interval, int attempts) {
        for (int ii = 0; ii < attempts; ii++) {
            try {
                new Socket (host, port);
                out.println (">>> Waiting " + (ii * interval) + " ms to STARTUP");
                return;
            }
            catch (IOException e) {
                sleep (interval);
            }
        }
    }

    void waitForShutdown () {
        waitForShutdown ("localhost", port);
    }

    private static void waitForShutdown (String aHost, int aPort) {
        waitForShutdown (aHost, aPort, 5, 100);
    }

    private static void waitForShutdown (
        String aHost, int aPort, long aInterval, int aAttempts) {

        for (int ii = 0; ii < aAttempts; ii++)
            try {
                new Socket (aHost, aPort);
                sleep (aInterval);
            }
            catch (IOException e) {
                out.println (">>> Waiting " + (ii * aInterval) + " ms to SHUTDOWN");
                return;
            }
    }

    void assertResponseContains (UrlResponse response, String body, int code) {
        assertEquals (code, response.status);
        assert response.body.contains (body);
    }

    void assertResponseEquals (UrlResponse response, String body, int code) {
        assertEquals (code, response.status);
        assertEquals (body, response.body);
    }

    @Override public String toString () {
        return format ("%s: %s %s <%s>",
            backend, secure? "SECURE" : "PLAIN", externalFiles? "FS" : "MEM", port);
    }
}
