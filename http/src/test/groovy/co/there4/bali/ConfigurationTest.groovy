package co.there4.bali

import org.testng.annotations.Test

import static Configuration.*

@Test public class ConfigurationTest {
    public void "loading an invalid url returns an empty map" () {
        assert url ("http://localhost:9999/invalid").isEmpty ()
    }

    public void "loading an url returns a map" () {
        String workDir = new File (".").absolutePath
        String file = "file://${workDir}/src/test/resources/configuration/valid.properties"

        Map<String, String> m = url (file)

        assert m.size () == 3 &&
            m.get("a").equals ("1") &&
            m.get("b").equals ("2") &&
            m.get("c").equals ("3")
    }

    public void "loading an non existent resource returns an empty map" () {
        assert resource ("/configuration/non-existent.properties").isEmpty ()
    }

    public void "loading a valid resource returns its keys" () {
        Map<String, String> m = resource ("/configuration/valid.properties")
        assert m.size () == 3 &&
            m.get("a").equals ("1") &&
            m.get("b").equals ("2") &&
            m.get("c").equals ("3")
    }

    public void "loading an non existent file returns an empty map" () {
        assert file ("src/test/resources/configuration/non-existent.properties").isEmpty ()
    }

    public void "loading a valid file returns its keys" () {
        Map<String, String> m = file ("src/test/resources/configuration/valid.properties")
        assert m.size () == 3 &&
            m.get("a").equals ("1") &&
            m.get("b").equals ("2") &&
            m.get("c").equals ("3")
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "loading system properties with a 'null' prefix throws an exception)" () {
        system (null);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "loading system properties with an empty prefix throws an exception)" () {
        system ("");
    }

    public void "loading system properties add only the ones that starts with prefix" () {
        System.setProperty ("settings.test.a", "1")
        System.setProperty ("settings.test.b", "2")
        System.setProperty ("settings.test.c", "3")
        System.setProperty ("settings.d", "4")
        Map<String, String> m = system ("settings.test")

        assert m.size () == 3 &&
            m.get("settings.test.a").equals ("1") &&
            m.get("settings.test.b").equals ("2") &&
            m.get("settings.test.c").equals ("3")
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "trying to load 'null' array of parameters throw an exception" () {
        parameters null
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "loading odd number of parameters will throw an exception" () {
        parameters ([ "--a" ] as String[])
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "loading parameters with incorrect format will throw an exception" () {
        parameters ([ "a", "1" ] as String[])
    }

    public void "loading correct parameters will return a map with them" () {
        Map<String, String> m = parameters ([ "--a", "1", "--b", "2", "--c", "3" ] as String[])

        assert m.size () == 3 &&
            m.get("a").equals ("1") &&
            m.get("b").equals ("2") &&
            m.get("c").equals ("3")
    }

    public void "loading different sources contain all the keys" () {
        Configuration s = configuration ()

        s.load (
            resource ("/configuration/valid.properties"),
            file ("src/test/resources/configuration/settings.properties")
        )

        assert s.getString ("a").equals ("1") &&
            s.get("b").equals ("2") &&
            s.get("c").equals ("3") &&
            s.get("str").equals ("abc") &&
            s.get("bool").equals ("true") &&
            s.get("int").equals ("1") &&
            s.get("float").equals (".1")
    }

    public void "loading sources with duplicated keys returns the value of the last ones" () {
        Configuration s = configuration ()

        s.load (
            file ("src/test/resources/configuration/settings.properties"),
            resource ("/configuration/settings-modified.properties")
        )

        assert s.get("str").equals ("cba") &&
            s.get("bool").equals ("false") &&
            s.get("int").equals ("2") &&
            s.get("float").equals (".2")
    }

    public void "the settings return all the keys available in the configuration" () {
        Configuration s = configuration ()
        s.load (resource ("/configuration/valid.properties"))
        assert s.keys ().contains ("a") &&
            s.keys ().contains ("b") &&
            s.keys ().contains ("c")
    }

    @SuppressWarnings ("GroovyPointlessBoolean")
    public void "getting parameters with a given class returns them with the correct type" () {
        Configuration s = configuration ()
        s.load (file ("src/test/resources/configuration/settings.properties"))

        assert s.getString("str").equals ("abc") &&
            s.getBoolean("bool") == true &&
            s.getInt("int") == 1 &&
            s.getLong("int") == 1L &&
            s.getByte("int") == (byte)1 &&
            s.getShort("int") == (short)1 &&
            s.getFloat("float") == 0.1f &&
            s.getDouble("float") == 0.1d
    }

    public void "an exception thrown while loading will result in an empty map" () {
        Map<String, String> map = loadStream (new InputStream () {
            @Override int read () throws IOException {
                throw new IOException ()
            }
        })

        assert map.isEmpty ()
    }
}

