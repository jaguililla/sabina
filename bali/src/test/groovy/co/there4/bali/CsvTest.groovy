package co.there4.bali

import org.testng.annotations.Test

class CsvTest {
    @Test void "Test empty line" () {
        assert Csv.parseLine ('').isEmpty ()
        assert Csv.parseLine (null).isEmpty ()
    }

    @Test void "Test white space outside quotes" () {
        final String line = '10, AU,Australia '
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == ' AU'
        assert result [2] == 'Australia '
    }

    @Test void "Test white space inside quotes" () {
        final String line = '"10"," AU","Australia "'
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == ' AU'
        assert result [2] == 'Australia '
    }

    @Test void "Test no quote" () {
        final String line = '10,AU,Australia'
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Australia'
    }

    @Test void "Test no quote but double quotes in column" () {
        final String line = '10,AU,Aus""tralia'
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Aus"tralia'
    }

    @Test void "Test parse whitespace" () {
        final String line = '"10","AU","Australia"\r\n'

        final List<String> row1 = Csv.parseLine (line)
        assert row1 != null && !row1.isEmpty ()
        assert row1.size () == 3
        assert row1 [0] == '10'
        assert row1 [1] == 'AU'
        assert row1 [2] == 'Australia'
    }

    @Test void "Test default quotes and separators " () {
        final String line = '"10","AU","Australia"'
        final List<String> result = Csv.parseLine (line, ' ' as char, ' ' as char)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Australia'
    }

    @Test void "Test double quotes" () {
        final String line = '"10","AU","Australia"'
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Australia'
    }

    @Test void "Test double quotes but double quotes in column" () {
        final String line = '"10","AU","Aus""tralia"'
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Aus"tralia'
    }

    @Test void "Test double quotes but comma in column" () {
        final String line = '"10","AU","Aus,tralia"'
        final List<String> result = Csv.parseLine (line)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Aus,tralia'
    }

    @Test void "Test custom separator" () {
        final String line = '10|AU|Australia'
        final List<String> result = Csv.parseLine (line, '|' as char)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Australia'
    }

    @Test void "Test custom separator and quote" () {
        final String line = "'10'|'AU'|'Australia'"
        final List<String> result = Csv.parseLine (line, '|' as char, '\'' as char)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Australia'
    }

    @Test void "Test custom separator and quote but custom quote in column" () {
        final String line = "'10'|'AU'|'Aus|tralia'"
        final List<String> result = Csv.parseLine (line, '|' as char, '\'' as char)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Aus|tralia'
    }

    @Test void "Test custom separator and quote but double quotes in column" () {
        final String line = "'10'|'AU'|'Aus\"\"tralia'"
        final List<String> result = Csv.parseLine (line, '|' as char, '\'' as char)

        assert result != null && !result.isEmpty ()
        assert result.size () == 3
        assert result [0] == '10'
        assert result [1] == 'AU'
        assert result [2] == 'Aus"tralia'
    }
}
