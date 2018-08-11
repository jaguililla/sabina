package co.there4.bali

import org.testng.annotations.Test

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate

class SslTest {
    @Test void 'Create a key store with a private key and certificate' () {
        final String keyResource = "ssl/bali.der"
        final String keyCertificateResource = "ssl/bali.crt"
        final String hostCertificateResource = "ssl/agung.crt"

        final KeyStore keyStore = Ssl.createKeyStore ()

        // Read the key file from disk and create a PrivateKey
        final InputStream keyInput = Io.getResourceStream (keyResource)
        final PrivateKey key = Ssl.readPrivateKey (keyInput)
        assert key.format == 'PKCS#8'
        assert key.algorithm == 'RSA'

        // Read the certificates from the files
        final InputStream keyCertificateInput = Io.getResourceStream (keyCertificateResource)
        final Certificate[] keyCertificateChain = Ssl.readCertificatesChain (keyCertificateInput)
        checkSingleCertificateChain keyCertificateChain

        final InputStream hostCertificateInput = Io.getResourceStream (hostCertificateResource)
        final Certificate[] hostCertificateChain = Ssl.readCertificatesChain (hostCertificateInput)
        final Certificate hostCertificate = hostCertificateChain[0]
        checkSingleCertificateChain hostCertificateChain

        Ssl.setCertificate (keyStore, hostCertificate)
        Ssl.setKey (keyStore, key, keyCertificateChain)

        KeyManagerFactory keyManagerFactory = Ssl.createKeyManagerFactory (keyStore)
        assert keyManagerFactory.algorithm == 'SunX509'

        X509TrustManager trustManager = Ssl.createTrustManager (keyStore)
        SSLContext sslContext = Ssl.createSslContext (keyManagerFactory, trustManager)
        assert sslContext.protocol == 'TLSv1.2'
    }

    private static void checkSingleCertificateChain (final Certificate[] certificateChain) {
        assert certificateChain.size () == 1
        assert certificateChain [0].type == 'X.509'
        assert certificateChain [0].publicKey.format == 'X.509'
        assert certificateChain [0].publicKey.algorithm == 'RSA'
    }
}
