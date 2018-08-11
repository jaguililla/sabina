package co.there4.bali;

import static co.there4.bali.Io.readInput;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import javax.net.ssl.*;
import javax.security.auth.x500.X500Principal;

public interface Ssl { // NOSONAR This interface holds constants used inside it
    String SSL_PROTOCOL = "TLSv1.2";
    String KEY_STORE_FORMAT = "PKCS12";
    String KEY_FORMAT = "RSA";
    String CERTIFICATE_FORMAT = "X509";
    String EMPTY_PASS = "";
    String DEFAULT_PRIVATE_KEY_ALIAS = "privateKey";

    static KeyStore createKeyStore () {
        return createKeyStore (EMPTY_PASS);
    }

    static KeyStore readKeyStore (final InputStream input, final String keyPassword) {
        return Unchecked.get (() -> {
            final KeyStore keyStore = KeyStore.getInstance (KEY_STORE_FORMAT);
            keyStore.load (input, keyPassword.toCharArray ());
            return keyStore;
        });
    }

    static KeyStore createKeyStore (final String keyPassword) {
        return readKeyStore (null, keyPassword);
    }

    static PrivateKey readPrivateKey (final InputStream keyInput) {
        return readPrivateKey (readInput (keyInput));
    }

    static PrivateKey readPrivateKey (final byte[] keyBytes) {
        return Unchecked.get (() -> {
            final KeyFactory keyFactory = KeyFactory.getInstance (KEY_FORMAT);
            return keyFactory.generatePrivate (new PKCS8EncodedKeySpec (keyBytes));
        });
    }

    static Collection<? extends Certificate> readCertificates ( // NOSONAR
        final InputStream certificateInput) {

        return Unchecked.get (() -> {
            final CertificateFactory certificateFactory =
                CertificateFactory.getInstance (CERTIFICATE_FORMAT);
            return certificateFactory.generateCertificates (certificateInput);
        });
    }

    static Certificate[] readCertificatesChain (final InputStream certificateInput) {
        return Unchecked.get (() -> {
            final Collection<? extends Certificate> certificates =
                readCertificates (certificateInput);
            final Iterator<? extends Certificate> certificatesIterator = certificates.iterator ();
            final Certificate[] chain = new Certificate[certificates.size ()];

            for (int ii = 0; ii < chain.length; ii++)
                chain[ii] = certificatesIterator.next ();

            return chain;
        });
    }

    static void setCertificate (final KeyStore keyStore, final Certificate certificate) {
        final X500Principal principal = ((X509Certificate)certificate).getSubjectX500Principal ();
        final String certificateAlias = principal.getName ();
        Unchecked.run (() -> keyStore.setCertificateEntry (certificateAlias, certificate));
    }

    static void setKey (
        final KeyStore keyStore,
        final PrivateKey key,
        final Certificate[] certificateChain) {

        setKey (keyStore, DEFAULT_PRIVATE_KEY_ALIAS, key, EMPTY_PASS, certificateChain);
    }

    static void setKey (
        final KeyStore keyStore,
        final String alias,
        final PrivateKey key,
        final String keyPassword,
        final Certificate[] certificateChain) {

        Unchecked.run (() ->
            keyStore.setKeyEntry (alias, key, keyPassword.toCharArray (), certificateChain)
        );
    }

    static SSLContext createSslContext (
        final KeyManagerFactory keyManagerFactory,
        final X509TrustManager trustManager) {

        return Unchecked.get (() -> {
            final SSLContext sslContext = SSLContext.getInstance (SSL_PROTOCOL);
            sslContext.init (
                keyManagerFactory == null? null : keyManagerFactory.getKeyManagers (),
                trustManager == null? null : new TrustManager[] { trustManager },
                null
            );
            return sslContext;
        });
    }

    static KeyManagerFactory createKeyManagerFactory (final KeyStore keyStore) {
        return createKeyManagerFactory (keyStore, EMPTY_PASS);
    }

    static KeyManagerFactory createKeyManagerFactory (
        final KeyStore keyStore, final String password) {

        if (keyStore == null)
            return null;

        return Unchecked.get (() -> {
            final String algorithm = KeyManagerFactory.getDefaultAlgorithm ();
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);

            keyManagerFactory.init(keyStore, password.toCharArray());
            return keyManagerFactory;
        });
    }

    static X509TrustManager createTrustManager (final KeyStore keyStore) {
        if (keyStore == null) {
            final String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm ();
            final TrustManagerFactory trustManagerFactory = Unchecked.get (() -> {
                final TrustManagerFactory tmf = TrustManagerFactory.getInstance (defaultAlgorithm);
                tmf.init((KeyStore)null);
                return tmf;
            });
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            if (trustManagers.length != 1
                || !(trustManagers[0] instanceof X509TrustManager)) {

                final String managers = Arrays.toString (trustManagers);
                throw new IllegalStateException("Unexpected default trust managers:" + managers);
            }

            return (X509TrustManager)trustManagers[0];
        }

        List<Certificate> certificates = new ArrayList<> ();
        Unchecked.run (() -> {
            final Enumeration<String> aliases = keyStore.aliases ();
            for (
                String alias = aliases.nextElement ();
                aliases.hasMoreElements ();
                alias = aliases.nextElement ()
            ) {
                certificates.add(keyStore.getCertificate (alias));
            }
        });

        return new X509TrustManager () {
            @Override
            public void checkClientTrusted (X509Certificate[] x509Certificates, String s) { // NOSONAR TODO Will be changed when SEMaaS host certs become OK
                // Implementation not needed
            }

            @Override
            public void checkServerTrusted (X509Certificate[] x509Certificates, String s) { // NOSONAR TODO Will be changed when SEMaaS host certs become OK
                // Implementation not needed
            }

            @Override public X509Certificate[] getAcceptedIssuers () {
                return certificates.stream ()
                    .filter (it -> it instanceof X509Certificate)
                    .map (it -> (X509Certificate)it)
                    .toArray (X509Certificate[]::new);
            }
        };
    }
}
