package spark.utils;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    // TODO OAuth
    // TODO HMAC

    public static String hmacSha1 (String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes ();
            SecretKeySpec signingKey = new SecretKeySpec (keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance ("HmacSHA1");
            mac.init (signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal (value.getBytes ());

            // Convert raw bytes to Hex
            byte[] hexBytes = Base64.getEncoder ().encode (rawHmac);

            //  Covert array of Hex bytes to a String
            return new String (hexBytes, "UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
