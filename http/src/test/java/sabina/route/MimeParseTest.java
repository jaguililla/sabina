/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.route;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static sabina.route.MimeParse.*;

import java.util.Collection;
import java.util.List;

import org.testng.annotations.Test;

/**
 * TODO Review ignored tests
 * TODO Change assert parameter's order (to TestNG)
 */
@Test public class MimeParseTest {
    @Test (enabled = false) public void testParseMediaRange() {
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

    @Test (enabled = false) public void testRFC2616Example() {
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

    public void testBestMatch() {
        List<String> supportedMimes = asList (
            "application/xbel+xml,application/xml".split (","));

        // direct match
        assertEquals(bestMatch (supportedMimes, "application/xbel+xml"), "application/xbel+xml");

        // direct match with a q parameter
        assertEquals(bestMatch (
            supportedMimes, "application/xbel+xml;q=1"), "application/xbel+xml");

        // direct match of our second choice with a q parameter
        assertEquals(bestMatch (supportedMimes, "application/xml;q=1"), "application/xml");

        // match using a subtype wildcard
        assertEquals(bestMatch (supportedMimes, "application/*;q=1"), "application/xml");

        // match using a type wildcard
        assertEquals(bestMatch (supportedMimes, "*/*"), "application/xml");

        supportedMimes = asList ("application/xbel+xml,text/xml".split (","));

        // match using a type versus a lower weighted subtype
        assertEquals(bestMatch (supportedMimes, "text/*;q=0.5,*/*;q=0.1"), "text/xml");

        // fail to match anything
        assertEquals(bestMatch (supportedMimes, "text/html,application/atom+xml; q=0.9"), "");

        // common AJAX scenario
        supportedMimes = asList ("application/json,text/html".split (","));
        assertEquals(bestMatch (
            supportedMimes, "application/json,text/javascript, */*"), "application/json");

        // verify fitness ordering
        assertEquals(bestMatch (
            supportedMimes, "application/json,text/html;q=0.9"), "application/json");
    }

    public void testSupportWildcards() {
        List<String> mimeTypesSupported = asList ("image/*,application/xml".split (","));

        // match using a type wildcard
        assertEquals(bestMatch (mimeTypesSupported, "image/png"), "image/*");
        // match using a wildcard for both requested and supported
        assertEquals(bestMatch (mimeTypesSupported, "image/*"), "image/*");
    }
}
