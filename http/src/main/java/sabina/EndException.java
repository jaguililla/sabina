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

import static co.there4.bali.Checks.require;

/**
 * Exception used for stopping the execution.
 *
 * @author Per Wendel
 */
public final class EndException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final int MIN_HTTP_CODE = 100;
    private static final int HTTP_OK = 200;

    public final int statusCode;
    public final String body;

    EndException () {
        this (HTTP_OK, null);
    }

    EndException (final int statusCode) {
        this (statusCode, null);
    }

    EndException (final String body) {
        this (HTTP_OK, body);
    }

    EndException (final int statusCode, final String body) {
        require (statusCode >= MIN_HTTP_CODE, "Invalid HTTP error code: " + statusCode);
        this.statusCode = statusCode;
        this.body = body;
    }
}
