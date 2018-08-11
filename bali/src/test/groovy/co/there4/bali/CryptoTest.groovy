package co.there4.bali

import org.testng.annotations.Test

import java.time.LocalDateTime

class CryptoTest {
    @Test void "Test empty line" () {
        final String key = "key"
        final LocalDateTime date = LocalDateTime.now ()
        final String formattedDate = date.format (Aws.DATE_FORMATTER)
        final String region = "region"
        final String service = "service"

        final byte[] signatureKey = Aws.signatureKey (key, date, region, service)
        final byte[] hmac =
            Crypto.chainHmac ("HmacSHA256", "AWS4$key", formattedDate, region, service, "aws4_request")

        assert hmac == signatureKey
    }
}
