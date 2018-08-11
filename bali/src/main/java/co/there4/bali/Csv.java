package co.there4.bali;

import java.util.ArrayList;
import java.util.List;

/**
 * https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java
 */
public interface Csv { // NOSONAR Constants are properly defined in this interface
    char DEFAULT_SEPARATOR = ',';
    char DEFAULT_QUOTE = '"';

    static List<String> parseLine (final String cvsLine) {
        return parseLine (cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    static List<String> parseLine (final String cvsLine, final char separators) {
        return parseLine (cvsLine, separators, DEFAULT_QUOTE);
    }

    static List<String> parseLine ( // NOSONAR Copied code (check Javadoc)
        final String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<> ();

        if (cvsLine == null || cvsLine.isEmpty ())
            return result;

        if (customQuote == ' ')
            customQuote = DEFAULT_QUOTE;

        if (separators == ' ')
            separators = DEFAULT_SEPARATOR;

        StringBuilder curVal = new StringBuilder ();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray ();

        for (char ch : chars) { // NOSONAR Copied code (check Javadoc)

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                }
                else {
                    // Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') { // NOSONAR Copied code (check Javadoc)
                        if (!doubleQuotesInColumn) {
                            curVal.append (ch);
                            doubleQuotesInColumn = true;
                        }
                    }
                    else {
                        curVal.append (ch);
                    }
                }
            }
            else {
                if (ch == customQuote) {

                    inQuotes = true;

                    // Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') { // NOSONAR Copied code
                        curVal.append ('"');
                    }

                    // Double quotes in column will hit this!
                    if (startCollectChar) { // NOSONAR Copied code (check Javadoc)
                        curVal.append ('"');
                    }
                }
                else if (ch == separators) {

                    result.add (curVal.toString ());

                    curVal = new StringBuilder ();
                    startCollectChar = false;
                }
                else if (ch == '\r') {
                    // Ignore LF characters
                    //noinspection UnnecessaryContinue Copied code (check Javadoc)
                    continue; // NOSONAR
                }
                else if (ch == '\n') {
                    // The end, break!
                    break;
                }
                else {
                    curVal.append (ch);
                }
            }
        }

        result.add (curVal.toString ());

        return result;
    }
}
