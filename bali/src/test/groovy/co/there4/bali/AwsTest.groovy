package co.there4.bali

import org.testng.annotations.Test

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

import static java.time.format.DateTimeFormatter.*

class AwsTest {
    static ClassLoader classLoader = ClassLoader.systemClassLoader

    static List<String> resources = [
        'get-header-key-duplicate',
        'get-header-value-order',
        'get-header-value-trim',
        'get-unreserved',
        'get-utf8',
        'get-vanilla',
        'get-vanilla-empty-query-key',
        'get-vanilla-query',
        'get-vanilla-query-order-key',
        'get-vanilla-query-order-key-case',
        'get-vanilla-query-order-value',
        'get-vanilla-query-unreserved',
        'get-vanilla-utf8-query',
        'post-header-key-case',
        'post-header-key-sort',
        'post-header-value-case',
        'post-vanilla',
        'post-vanilla-empty-query-value',
        'post-vanilla-query',
        'post-x-www-form-urlencoded',
        'post-x-www-form-urlencoded-parameters'
    ]

    @Test void "Empty body hash" () {
        assert Strings.hex(Crypto.hash(Aws.HASH_ALGORITHM, "")) == Aws.EMPTY_BODY_HASH
    }

    static String[] readRequestResource (final String resourceBase) {
        final String requestText = classLoader.getResourceAsStream ("${resourceBase}.req").text
        return requestText.split ('\n')
    }

    @Test void "AWS test suite" () {
        final String region = "us-east-1"
        final String service = "service"
        final String accessKey = "AKIDEXAMPLE"
        final String secretKey = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY"

        for (final String resource : resources) {
            println "<<<<<< ${resource.toUpperCase ()} >>>>>>"
            final String resourceBase = "aws-sig-v4-test-suite/$resource/$resource"

            // Load Request
            final String[] lines = readRequestResource (resourceBase)
            final String request = lines.first ()[0 .. ('HTTP/1.1'.length () + 1) * -1].trim ()
            final int firstSpace = request.indexOf ' '
            final String method = request[0 .. firstSpace].trim ()
            final String path = request.substring (firstSpace).trim ()
            final int linesCount = lines.length
            final List<String> headersAndBody = lines[1 .. linesCount - 1]
            final int bodyIndex = headersAndBody.indexOf ('')
            final List<String> headersLines = bodyIndex == -1? headersAndBody : headersAndBody[0 .. bodyIndex - 1]
            final Map<String, List<?>> headers = headersLines
                .collect {
                    final int separator = it.indexOf (':')
                    final String key = it[0 .. separator - 1]
                    final String value = it.substring (separator + 1)
                    new MapEntry (key, value)
                }
                .groupBy { it.key }
                .collectEntries {
                    [ (it.key.toString ()) : it.value.collect { me -> me.value } ]
                }
            final String payload = bodyIndex == -1? '' : headersAndBody[bodyIndex + 1] // Only works for a single line
            final String host = headers['Host'][0] ?: ''
            final String dateHeader = headers['X-Amz-Date'][0]
            final LocalDateTime date = LocalDateTime.parse (dateHeader, Aws.DATE_TIME_FORMATTER)
            final String url = "http://$host$path"

            // Canonical Request
            final String canonicalRequest = Aws.canonicalRequest (method, url, date, headers, payload)
            assert canonicalRequest == classLoader.getResourceAsStream ("${resourceBase}.creq").text

            // String to Sign
            final String stringToSign = Aws.stringToSign (date, region, service, canonicalRequest)
            assert stringToSign == classLoader.getResourceAsStream ("${resourceBase}.sts").text

            // Calculate Signature
            final byte[] signatureKey = Aws.signatureKey (secretKey, date, region, service)
            final String signature = Aws.calculateSignature (stringToSign, signatureKey)
            final String authorization =
                Aws.createAuthorization (accessKey, date, region, service, headers, signature)
            assert authorization == classLoader.getResourceAsStream ("${resourceBase}.authz").text
        }
    }

    @Test void "Dates scrapbook" () {
        println LocalDateTime.now ().format (ISO_DATE_TIME)
        println LocalDateTime.now ().format (BASIC_ISO_DATE)
        println LocalDateTime.now ().format (ISO_DATE)
        println LocalDateTime.now ().format (ISO_TIME)
        println LocalDateTime.now ().format (ofPattern ("HHmmss"))
        println ZonedDateTime.now (ZoneId.of ("UTC")).format (ofPattern ("HHmmss"))
        println ZonedDateTime.now (ZoneId.of ("UTC")).format (ofPattern ("yyyyMMdd'T'HHmmss"))
    }
}
