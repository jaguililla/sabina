package co.there4.bali

import org.testng.annotations.Test

import static Io.*
import static java.lang.System.lineSeparator

@Test class IoTest {
    private final static String RESOURCE_CONTENTS = "resource file${lineSeparator ()}second line"
    public static final int READED_RESOURCE_SIZE = 35

    @Test void "reading a resource from a path returns its contents" () {
        assert read ("co/there4/bali/resource.txt") == RESOURCE_CONTENTS
    }

    @Test void "reading a resource from a stream returns its contents" () {
        String r = read (Class.getResourceAsStream ("/co/there4/bali/resource.txt"))
        assert r == RESOURCE_CONTENTS
    }

    @Test void "reading a resource from a URL returns its contents" () {
        String r = read (Class.getResource ("/co/there4/bali/resource.txt"))
        assert r == RESOURCE_CONTENTS
    }

    @Test (expectedExceptions = RuntimeException.class)
    void "reading a resource from an invalid URL throws an exception" () {
        read (new URL ("http://localhost:0/error"))
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "if a resource is not found it is listed in the exception" () {
        try {
            read ("not-found.txt")
        }
        catch (IllegalArgumentException e) {
            assert e.getMessage ().contains ("not-found.txt")
            throw e
        }
    }

    @Test (expectedExceptions = RuntimeException)
    void "an IO exception reading an input stream will be converted in a runtime one" () {
        read (new InputStream () {
            @Override int read () throws IOException {
                throw new IOException ()
            }
        })
    }

    @Test void 'Read a data stream works as expected' () {
        final InputStream stream = getResourceStream ('multiline_resource.txt')
        final DataInputStream dataStream = new DataInputStream (stream)
        final byte[] input = readInput (dataStream)
        final String inputString = new String (input)
        assert inputString.length () == READED_RESOURCE_SIZE
    }

    @Test void 'Read a resource as a string works as expected' () {
        final InputStream stream = getResourceStream ('multiline_resource.txt')
        final byte[] input = readInput (stream)
        final String inputString = new String (input)

        final String base64Encoded = Base64.getEncoder ().encodeToString (input)
        final String base64Decoded = new String (Base64.getDecoder ().decode (base64Encoded))

        assert inputString.length () == READED_RESOURCE_SIZE
        assert inputString == base64Decoded
    }
}
