package co.there4.bali;

import static co.there4.bali.Strings.utf8Bytes;

import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public interface Crypto {
    static byte[] hmac (
        final String algorithm, final byte[] data, final byte[] key) {

        return Unchecked.get (() -> {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec (key, algorithm));
            return mac.doFinal(data);
        });
    }

    static byte[] chainHmac (final String algorithm, final String... fields) {
        return Arrays.stream (fields)
            .map (Strings::utf8Bytes)
            .reduce ((data, field) -> hmac (algorithm, field, data))
            .orElse (new byte[0]);
    }

    static byte[] hash (final String algorithm, final byte[] data) {
        return Unchecked.get (() -> {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(data);
            return messageDigest.digest();
        });
    }

    static byte[] hash (final String algorithm, final String data) {
        return hash (algorithm, utf8Bytes (data));
    }

    static byte[] sign (final String algorithm, final String data, final byte[] key) {
        return Unchecked.get (() -> {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(utf8Bytes (data));
        });
    }
}
