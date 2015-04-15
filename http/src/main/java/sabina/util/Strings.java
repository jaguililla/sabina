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

package sabina.util;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.io.UnsupportedEncodingException;

/**
 * TODO .
 *
 * @author jam
 */
public final class Strings {
    public static boolean isNullOrEmpty (String str) {
        return com.google.common.base.Strings.isNullOrEmpty (str);
    }

    public static String encode (final byte[] body, final String encoding) {
        return ofNullable(encoding).map(enc -> {
            try {
                return body == null? "" : new String(body, enc);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }).orElse(new String(body));
    }

    public static byte[] decode (final String text, final String encoding) {
        if (text == null)
            return new byte[0];

        if (encoding == null)
            return text.getBytes();

        try {
            return text.getBytes(encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(format("Error decoding '%s' with '%s'", text, encoding), e);
        }
    }

    private Strings () {
        throw new IllegalStateException ();
    }
}
