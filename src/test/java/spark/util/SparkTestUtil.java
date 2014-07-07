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

package spark.util;

import static java.lang.System.out;
import static org.apache.http.conn.socket.PlainConnectionSocketFactory.INSTANCE;
import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

public class SparkTestUtil {

    public static class UrlResponse {
        public Map<String, String> headers;
        public String body;
        public int status;
    }

    private int port;

    private CloseableHttpClient httpClient;

    public SparkTestUtil (int aPort) {
        this.port = aPort;

        SSLConnectionSocketFactory sslConnectionSocketFactory =
            new SSLConnectionSocketFactory (getSslFactory (), ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory>create ()
                .register ("http", INSTANCE)
                .register ("https", sslConnectionSocketFactory)
                .build ();

        BasicHttpClientConnectionManager connManager =
            new BasicHttpClientConnectionManager (socketFactoryRegistry,
                new ManagedHttpClientConnectionFactory ());

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
            .build ();
    }

    public UrlResponse doMethodSecure (String requestMethod, String path) {
        return doMethod (requestMethod, path, null, true, "text/html");
    }

    public UrlResponse doMethodSecure (String requestMethod, String path, String body) {
        return doMethod (requestMethod, path, body, true, "text/html");
    }

    public UrlResponse doMethod (String requestMethod, String path) {
        return doMethod (requestMethod, path, null, false, "text/html");
    }

    public UrlResponse doMethod (String requestMethod, String path, String body) {
        return doMethod (requestMethod, path, body, false, "text/html");
    }

    public UrlResponse doMethodSecure (
        String requestMethod, String path, String body, String acceptType) {
        return doMethod (requestMethod, path, body, true, acceptType);
    }

    public UrlResponse doMethod (
        String requestMethod, String path, String body, String acceptType) {
        return doMethod (requestMethod, path, body, false, acceptType);
    }

    private UrlResponse doMethod (
        String requestMethod,
        String path,
        String body,
        boolean secureConnection,
        String acceptType) {

        try {
            HttpUriRequest httpRequest =
                getHttpRequest (requestMethod, path, body, secureConnection, acceptType);
            HttpResponse httpResponse = httpClient.execute (httpRequest);

            UrlResponse urlResponse = new UrlResponse ();
            urlResponse.status = httpResponse.getStatusLine ().getStatusCode ();

            HttpEntity entity = httpResponse.getEntity ();
            if (entity != null)
                urlResponse.body = EntityUtils.toString (entity);
            else
                urlResponse.body = "";

            Map<String, String> headers = new HashMap<> ();
            Header[] allHeaders = httpResponse.getAllHeaders ();
            for (Header header : allHeaders)
                headers.put (header.getName (), header.getValue ());

            urlResponse.headers = headers;
            return urlResponse;
        }
        catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    private HttpUriRequest getHttpRequest (
        String requestMethod,
        String path,
        String body,
        boolean secureConnection,
        String acceptType) {

        if (body == null)
            body = "";

        try {
            String protocol = secureConnection? "https" : "http";
            String uri = protocol + "://localhost:" + port + path;

            if (requestMethod.equals ("GET")) {
                HttpGet httpGet = new HttpGet (uri);

                httpGet.setHeader ("Accept", acceptType);

                return httpGet;
            }

            if (requestMethod.equals ("POST")) {
                HttpPost httpPost = new HttpPost (uri);
                httpPost.setHeader ("Accept", acceptType);
                httpPost.setEntity (new StringEntity (body));
                return httpPost;
            }

            if (requestMethod.equals ("PATCH")) {
                HttpPatch httpPatch = new HttpPatch (uri);
                httpPatch.setHeader ("Accept", acceptType);
                httpPatch.setEntity (new StringEntity (body));
                return httpPatch;
            }

            if (requestMethod.equals ("DELETE")) {
                HttpDelete httpDelete = new HttpDelete (uri);
                httpDelete.setHeader ("Accept", acceptType);
                return httpDelete;
            }

            if (requestMethod.equals ("PUT")) {
                HttpPut httpPut = new HttpPut (uri);
                httpPut.setHeader ("Accept", acceptType);
                httpPut.setEntity (new StringEntity (body));
                return httpPut;
            }

            if (requestMethod.equals ("HEAD"))
                return new HttpHead (uri);

            if (requestMethod.equals ("TRACE"))
                return new HttpTrace (uri);

            if (requestMethod.equals ("OPTIONS"))
                return new HttpOptions (uri);

            throw new IllegalArgumentException ("Unknown method " + requestMethod);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    public int getPort () {
        return port;
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
        KeyStore keyStore = null;

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
     * Return JVM param set keystore or default if not set.
     *
     * @return Keystore location as string
     */
    public static String getKeyStoreLocation () {
        String keyStoreLoc = System.getProperty ("javax.net.ssl.keyStore");
        return keyStoreLoc == null? "./src/test/resources/keystore.jks" : keyStoreLoc;
    }

    /**
     * Return JVM param set keystore password or default if not set.
     *
     * @return Keystore password as string
     */
    public static String getKeystorePassword () {
        String password = System.getProperty ("javax.net.ssl.keyStorePassword");
        return password == null? "password" : password;
    }

    /**
     * Return JVM param set truststore location, or keystore location if not
     * set. if keystore not set either, returns default
     *
     * @return truststore location as string
     */
    public static String getTrustStoreLocation () {
        String trustStoreLoc = System.getProperty ("javax.net.ssl.trustStore");
        return trustStoreLoc == null? getKeyStoreLocation () : trustStoreLoc;
    }

    /**
     * Return JVM param set truststore password or keystore password if not set.
     * If still not set, will return default password
     *
     * @return truststore password as string
     */
    public static String getTrustStorePassword () {
        String password = System.getProperty ("javax.net.ssl.trustStorePassword");
        return password == null? getKeystorePassword () : password;
    }

    public static void sleep (long time) {
        try {
            Thread.sleep (time);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public void waitForStartup () {
        waitForStartup ("localhost", getPort ());
    }

    public static void waitForStartup (String aHost, int aPort) {
        waitForStartup (aHost, aPort, 5, 100);
    }

    public static void waitForStartup (
        String aHost, int aPort, long aInterval, int aAttempts) {

        for (int ii = 0; ii < aAttempts; ii++)
            try {
                new Socket (aHost, aPort);
                out.println (">>> Waiting " + (ii * aInterval) + " ms to STARTUP");
                return;
            }
            catch (IOException e) {
                sleep (aInterval);
            }
    }

    public void waitForShutdown () {
        waitForShutdown ("localhost", getPort ());
    }

    public static void waitForShutdown (String aHost, int aPort) {
        waitForShutdown (aHost, aPort, 5, 100);
    }

    public static void waitForShutdown (
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
}
