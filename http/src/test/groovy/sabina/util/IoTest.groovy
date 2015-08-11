package sabina.util

import org.testng.annotations.Test

import static sabina.util.Io.*

@Test class IoTest {
    private final static String RESOURCE_CONTENTS = "resource file" + Strings.EOL + "second line"

    public void "the default class loader should be the system one" () {
        assert classLoader() == ClassLoader.getSystemClassLoader ()
    }

    public void "reading a resource from a path returns its contents" () {
        assert read ("sabina/util/resource.txt").equals (RESOURCE_CONTENTS)
    }

    public void "reading a resource from a stream returns its contents" () {
        String r = read (Class.getResourceAsStream ("/sabina/util/resource.txt"))
        assert r.equals (RESOURCE_CONTENTS)
    }

    public void "using a different class loader resolves resources nicely" () {
        classLoader (Io.class)
        String r = read ("sabina/util/resource.txt")
        assert r.equals (RESOURCE_CONTENTS)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "is is not allowed to set a 'null' class for loading" () {
        classLoader ((Class<?>)null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "is is not allowed to set a 'null' class loader" () {
        classLoader ((ClassLoader)null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "if a resource is not found it is listed in the exception" () {
        try {
            read ("not-found.txt")
        }
        catch (IllegalArgumentException e) {
            assert e.getMessage ().contains ("not-found.txt")
            throw e
        }
    }
}
