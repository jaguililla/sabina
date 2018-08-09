package co.there4.bali

import org.testng.annotations.Test

import static Configuration.*

@Test class ConfigurationTest {
    @Test void "loading an invalid url returns an empty map" () {
        assert url ("http://localhost:9999/invalid").isEmpty ()
    }

    @Test void "loading an url returns a map" () {
        String workDir = new File (".").absolutePath
        String file = "file://${workDir}/src/test/resources/configuration/valid.properties"

        Map<String, Object> m = url (file)

        assert m.size () == 3 &&
            m.get ("a") == "1" &&
            m.get ("b") == "2" &&
            m.get ("c") == "3"
    }

    @Test void "loading an non existent resource returns an empty map" () {
        assert resource ("/configuration/non-existent.properties").isEmpty ()
    }

    @Test void "loading a valid resource returns its keys" () {
        Map<String, Object> m = resource ("/configuration/valid.properties")
        assert m.size () == 3 &&
            m.get ("a") == "1" &&
            m.get ("b") == "2" &&
            m.get ("c") == "3"
    }

    @Test void "loading an non existent file returns an empty map" () {
        assert file ("src/test/resources/configuration/non-existent.properties").isEmpty ()
    }

    @Test void "loading a valid file returns its keys" () {
        Map<String, Object> m = file ("src/test/resources/configuration/valid.properties")
        assert m.size () == 3 &&
            m.get ("a") == "1" &&
            m.get ("b") == "2" &&
            m.get ("c") == "3"
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "loading system properties with a 'null' prefix throws an exception)" () {
        system (null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "loading system properties with an empty prefix throws an exception)" () {
        system ("")
    }

    @Test void "loading system properties add only the ones that starts with prefix" () {
        System.setProperty ("settings.test.a", "1")
        System.setProperty ("settings.test.b", "2")
        System.setProperty ("settings.test.c", "3")
        System.setProperty ("settings.d", "4")
        Map<String, Object> m = system ("settings.test")

        assert m.size () == 3 &&
            m.get ("settings.test.a") == "1" &&
            m.get ("settings.test.b") == "2" &&
            m.get ("settings.test.c") == "3"
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "trying to load 'null' array of parameters throw an exception" () {
        parameters null
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "loading odd number of parameters will throw an exception" () {
        parameters ([ "--a" ] as String[])
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "loading parameters with incorrect format will throw an exception" () {
        parameters ([ "a", "1" ] as String[])
    }

    @Test void "loading correct parameters will return a map with them" () {
        Map<String, Object> m = parameters ([ "--a", "1", "--b", "2", "--c", "3" ] as String[])

        assert m.size () == 3 &&
            m.get ("a") == "1" &&
            m.get ("b") == "2" &&
            m.get ("c") == "3"
    }

    @Test void "loading different sources contain all the keys" () {
        Configuration s = configuration ()

        s.load (
            resource ("/configuration/valid.properties"),
            file ("src/test/resources/configuration/settings.properties")
        )

        assert s.getString ("a") == "1" &&
            s.get ("b") == "2" &&
            s.get ("c") == "3" &&
            s.get ("str") == "abc" &&
            s.get ("bool") == "true" &&
            s.get ("int") == "1" &&
            s.get ("float") == ".1"
    }

    @Test void "loading sources with duplicated keys returns the value of the last ones" () {
        Configuration s = configuration ()

        s.load (
            file ("src/test/resources/configuration/settings.properties"),
            resource ("/configuration/settings-modified.properties")
        )

        assert s.get ("str") == "cba" &&
            s.get ("bool") == "false" &&
            s.get ("int") == "2" &&
            s.get ("float") == ".2"
    }

    @Test void "the settings return all the keys available in the configuration" () {
        Configuration s = configuration ()
        s.load (resource ("/configuration/valid.properties"))
        assert s.keys ().contains ("a") &&
            s.keys ().contains ("b") &&
            s.keys ().contains ("c")
    }

    @SuppressWarnings ("GroovyPointlessBoolean")
    @Test void "getting parameters with a given class returns them with the correct type" () {
        Configuration s = configuration ()
        s.load (file ("src/test/resources/configuration/settings.properties"))

        assert s.getString ("str") == "abc" &&
            s.getBoolean("bool") == true &&
            s.getInt("int") == 1 &&
            s.getLong("int") == 1L &&
            s.getByte("int") == (byte)1 &&
            s.getShort("int") == (short)1 &&
            s.getFloat("float") == 0.1f &&
            s.getDouble("float") == 0.1d
    }

    @Test void "an exception thrown while loading will result in an empty map" () {
        Map<String, Object> map = loadStream (new InputStream () {
            @Override int read () throws IOException {
                throw new IOException ()
            }
        })

        assert map.isEmpty ()
    }
}

