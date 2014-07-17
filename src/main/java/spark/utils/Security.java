/*
 * Copyright © 2014 Juan José Aguililla. All rights reserved.
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
