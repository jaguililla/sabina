package co.there4.bali

import org.testng.annotations.Test

import static Io.*

@Test class IoTest {
    private final static String RESOURCE_CONTENTS = "resource file" + Strings.EOL + "second line"

    public void "reading a resource from a path returns its contents" () {
        assert read ("co/there4/bali/resource.txt").equals (RESOURCE_CONTENTS)
    }

    public void "reading a resource from a stream returns its contents" () {
        String r = read (Class.getResourceAsStream ("/co/there4/bali/resource.txt"))
        assert r.equals (RESOURCE_CONTENTS)
    }

    public void "reading a resource from a URL returns its contents" () {
        String r = read (Class.getResource ("/co/there4/bali/resource.txt"))
        assert r == RESOURCE_CONTENTS
    }

    @Test (expectedExceptions = RuntimeException.class)
    public void "reading a resource from an invalid URL throws an exception" () {
        read (new URL ("http://localhost:0/error"));
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "if a resource is not found it is listed in the exception" () {
        try {
            read ("not-found.txt")
        }
        catch (IllegalArgumentException e) {
            assert e.getMessage ().contains ("not-found.txt")
            throw e
        }
    }

    @Test (expectedExceptions = RuntimeException)
    public void "an IO exception reading an input stream will be converted in a runtime one" () {
        read (new InputStream () {
            @Override int read () throws IOException {
                throw new IOException ()
            }
        })
    }
}
