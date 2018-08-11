package co.there4.bali;

import static co.there4.bali.Builders.entry;
import static co.there4.bali.Checks.requireNotEmpty;
import static co.there4.bali.Checks.requireNotNull;
import static co.there4.bali.Crypto.*;
import static co.there4.bali.Http.encode;
import static co.there4.bali.Http.parseQueryString;
import static co.there4.bali.Strings.hex;
import static co.there4.bali.Strings.isBlank;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface Aws { // NOSONAR This interface is not intended to be implemented
    String AWS_REQUEST = "aws4_request";
    String SIGNATURE_KEY_PREFIX = "AWS4";
    String EMPTY_BODY_HASH = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    String STRING_TO_SIGN_ALGORITHM = "AWS4-HMAC-SHA256";
    String HASH_ALGORITHM = "SHA-256";
    String HMAC_ALGORITHM = "HmacSHA256";

    String DATE_FORMAT = "yyyyMMdd";
    String TIME_FORMAT = "HHmmss";
    String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" + TIME_FORMAT + "'Z'";
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern (DATE_FORMAT);
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern (DATE_TIME_FORMAT);
    String AUTHORIZATION_FORMAT = "%s Credential=%s/%s/%s/%s/%s, SignedHeaders=%s, Signature=%s";

    String DATE_HEADER = "x-amz-date";
    String HOST_HEADER = "host";

    String LINE_SEPARATOR = "\n";
    String KEY_VALUE_SEPARATOR = "=";
    String QUERY_PARAM_SEPARATOR = "&";
    String HEADER_SEPARATOR = ":";
    String SIGNED_HEADER_SEPARATOR = ";";

    static String canonicalRequest (
        final String method,
        final URL url,
        final LocalDateTime date,
        final Map<String, List<?>> headers,
        final String payload) {

        requireNotEmpty (method, "method");
        requireNotNull (url, "url");
        requireNotNull (date, "date");
        requireNotNull (headers, "headers");
        requireNotNull (payload, "payload");

        return Unchecked.get(() -> {
            final String host = url.getHost ();
            final String path = url.getPath ();
            final String query = url.getQuery ();

            headers.put(DATE_HEADER, singletonList (date.format (DATE_TIME_FORMATTER)));
            headers.put(HOST_HEADER, singletonList (host));

            return String.join (LINE_SEPARATOR,
                method,
                encodePath (path),
                isBlank (query)? "" : canonicalQueryString (query),
                canonicalHeaders (headers),
                "",
                signedHeaders (headers),
                payload == null || payload.isEmpty ()? EMPTY_BODY_HASH : hexHash (payload)
            );
        });
    }

    static String canonicalRequest (
        final String method,
        final String url,
        final LocalDateTime date,
        final Map<String, List<?>> headers,
        final String payload) {

        return canonicalRequest (method, Unchecked.get(() -> new URL(url)), date, headers, payload);
    }

    static String stringToSign (
        final String algorithm,
        final LocalDateTime date,
        final String region,
        final String service,
        final String canonicalRequest) {

        requireNotEmpty (algorithm, "algorithm");
        requireNotNull (date, "date");
        requireNotEmpty (region, "region");
        requireNotEmpty (service, "service");
        requireNotEmpty (canonicalRequest, "canonicalRequest");

        return String.join (LINE_SEPARATOR,
            algorithm,
            date.format (DATE_TIME_FORMATTER),
            date.format (DATE_FORMATTER) + '/' + region + '/' + service + '/' + AWS_REQUEST,
            hexHash (canonicalRequest)
        );
    }

    static String stringToSign (
        final LocalDateTime date,
        final String region,
        final String service,
        final String canonicalRequest) {

        return stringToSign (STRING_TO_SIGN_ALGORITHM, date, region, service, canonicalRequest);
    }

    static byte[] signatureKey (
        final String key,
        final LocalDateTime date,
        final String region,
        final String service) {

        requireNotEmpty (key, "key");
        requireNotNull (date, "date");
        requireNotEmpty (region, "region");
        requireNotEmpty (service, "service");

        final String formattedDate = date.format (DATE_FORMATTER);
        final String awsKey = SIGNATURE_KEY_PREFIX + key;
        return chainHmac (HMAC_ALGORITHM, awsKey, formattedDate, region, service, AWS_REQUEST);
    }

    static String calculateSignature (final String stringToSign, final byte[] signatureKey) {
        return hex(sign (HMAC_ALGORITHM, stringToSign, signatureKey));
    }

    static String createAuthorization (
        final String algorithm,
        final String accessKey,
        final LocalDateTime date,
        final String region,
        final String service,
        final Map<String, ?> headers,
        final String signature) {

        final String formattedDate = date.format (DATE_FORMATTER);
        return String.format (
            AUTHORIZATION_FORMAT,
            algorithm,
            accessKey,
            formattedDate,
            region,
            service,
            AWS_REQUEST,
            signedHeaders (headers),
            signature
        );
    }

    static String createAuthorization (
        final String accessKey,
        final LocalDateTime date,
        final String region,
        final String service,
        final Map<String, ?> headers,
        final String signature) {

        return createAuthorization (
            STRING_TO_SIGN_ALGORITHM, accessKey, date, region, service, headers, signature);
    }

    static String encodePath (final String path) {
        return encode (path)
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%2F", "/")
            .replace("%7E", "~");
    }

    static String canonicalQueryString (final String query) {
        return parseQueryString (query).stream ()
            .map (it ->
                entry(
                    encodePath(it.getKey ().trim ()),
                    encodePath(it.getValue ().trim ())
                )
            )
            .map (it -> it.getKey () + KEY_VALUE_SEPARATOR + it.getValue ())
            .sorted ()
            .collect (joining (QUERY_PARAM_SEPARATOR));
    }

    static String canonicalHeaders (Map<String, List<?>> headers) {
        return headers.entrySet ().stream ()
            .map (it ->
                entry(
                    encodePath(it.getKey ().toLowerCase ().trim ()),
                    it.getValue ().stream ()
                        .map (Object::toString)
                        .map (String::trim)
                        .map (Aws::trimAllSpaces)
                        .collect (joining(","))
                )
            )
            .distinct ()
            .map (it -> it.getKey () + HEADER_SEPARATOR + it.getValue ())
            .sorted ()
            .collect (joining (LINE_SEPARATOR));
    }

    static String trimAllSpaces (String s) {
        return Arrays.stream (s.split (" "))
            .filter (it -> !isBlank (it))
            .map (String::trim)
            .collect (joining(" "));
    }

    static String signedHeaders (Map<String, ?> headers) {
        return headers.keySet ().stream ()
            .map (it -> encodePath(it.toLowerCase ().trim ()))
            .distinct ()
            .sorted ()
            .collect (joining (SIGNED_HEADER_SEPARATOR));
    }

    static String hexHash (final String payload) {
        return hex(hash(HASH_ALGORITHM, payload));
    }
}
