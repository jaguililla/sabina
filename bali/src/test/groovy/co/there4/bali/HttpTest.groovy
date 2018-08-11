package co.there4.bali

import org.testng.annotations.Test

class HttpTest {
    @Test void "Parse key only query parameters return correct data" () {
        assert Http.parseQueryString ("a=1&b&c&d=e") == [
            a : "1",
            b : "",
            c : "",
            d : "e"
        ].entrySet ().toList ()
    }
}
