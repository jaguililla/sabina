/*
 * Copyright © 2011 Per Wendel. All rights reserved.
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

package sabina;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Exception used for stopping the execution.
 *
 * @author Per Wendel
 */
public final class HaltException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public final int statusCode;
    public final String body;

    HaltException () {
        this (SC_OK, null);
    }

    HaltException (int statusCode) {
        this (statusCode, null);
    }

    HaltException (String body) {
        this (SC_OK, body);
    }

    HaltException (int statusCode, String body) {
        if (statusCode < 100)
            throw new IllegalArgumentException ("Invalid HTTP error code: " + statusCode);

        this.statusCode = statusCode;
        this.body = body;
    }
}
