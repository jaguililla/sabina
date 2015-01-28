package sabina.route;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static sabina.route.MimeParse.fitnessAndQualityParsed;
import static sabina.route.MimeParse.parseMediaRange;
import static sabina.route.MimeParse.parseMimeType;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class MimeParseTest {
    @Test public void testParseMediaRange() {
        assertEquals("('application', 'xml', {'q':'1',})",
            parseMediaRange ("application/xml;q=1").toString());
        assertEquals("('application', 'xml', {'q':'1',})",
            parseMediaRange ("application/xml").toString());
        assertEquals("('application', 'xml', {'q':'1',})",
            parseMediaRange ("application/xml;q=").toString());
        assertEquals("('application', 'xml', {'q':'1',})",
            parseMediaRange ("application/xml ; q=").toString());
        assertEquals("('application', 'xml', {'b':'other','q':'1',})",
            parseMediaRange ("application/xml ; q=1;b=other").toString ());
        assertEquals("('application', 'xml', {'b':'other','q':'1',})",
            parseMediaRange ("application/xml ; q=2;b=other").toString ());

        // Java URLConnection class sends an Accept header that includes a single *
        assertEquals("('*', '*', {'q':'.2',})", parseMediaRange (" *; q=.2").toString());
    }

    @Test public void testRFC2616Example() {
        Collection<MimeParse.ParseResults> accept = asList (
            parseMimeType ("text/*;q=0.3"),
            parseMimeType ("text/html;q=0.7"),
            parseMimeType ("text/html;level=1"),
            parseMimeType ("text/html;level=2;q=0.4"),
            parseMimeType ("*/*;q=0.5")
        );

        assertEquals(1.0f, fitnessAndQualityParsed ("text/html;level=1", accept).quality, 0.01);
        assertEquals(0.7f, fitnessAndQualityParsed ("text/html", accept).quality, 0.01);
        assertEquals(0.3f, fitnessAndQualityParsed ("text/plain", accept).quality, 0.01);
        assertEquals(0.5f, fitnessAndQualityParsed ("image/jpeg", accept).quality, 0.01);
        assertEquals(0.4f, fitnessAndQualityParsed ("text/html;level=2", accept).quality, 0.01);
        assertEquals(0.7f, fitnessAndQualityParsed ("text/html;level=3", accept).quality, 0.01);
    }

    @Test public void testBestMatch() {
        List<String> mimeTypesSupported = asList (
            "application/xbel+xml,application/xml".split (","));

        // direct match
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "application/xbel+xml"), "application/xbel+xml");

        // direct match with a q parameter
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "application/xbel+xml;q=1"), "application/xbel+xml");

        // direct match of our second choice with a q parameter
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "application/xml;q=1"), "application/xml");

        // match using a subtype wildcard
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "application/*;q=1"), "application/xml");

        // match using a type wildcard
        assertEquals(MimeParse.bestMatch(mimeTypesSupported, "*/*"),
            "application/xml");

        mimeTypesSupported = asList (
            "application/xbel+xml,text/xml".split (","));

        // match using a type versus a lower weighted subtype
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "text/*;q=0.5,*/*;q=0.1"), "text/xml");

        // fail to match anything
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "text/html,application/atom+xml; q=0.9"), "");

        // common AJAX scenario
        mimeTypesSupported = asList ("application/json,text/html".split (","));
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "application/json,text/javascript, */*"), "application/json");

        // verify fitness ordering
        assertEquals(MimeParse.bestMatch(mimeTypesSupported,
            "application/json,text/html;q=0.9"), "application/json");
    }

    @Test public void testSupportWildcards() {
        List<String> mimeTypesSupported = asList ("image/*,application/xml".split (","));

        // match using a type wildcard
        assertEquals(MimeParse.bestMatch(mimeTypesSupported, "image/png"), "image/*");
        // match using a wildcard for both requested and supported
        assertEquals(MimeParse.bestMatch(mimeTypesSupported, "image/*"), "image/*");
    }
}
