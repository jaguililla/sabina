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

package spark;

import java.util.function.BiConsumer;

public class ExceptionHandler<T extends Exception> {
    /** Holds the type of exception that this filter will handle */
    final Class<T> exceptionClass;
    final BiConsumer<T, Context> mHandler;

    /**
     * Initializes the filter with the provided exception type
     *
     * @param aException Type of exception
     */
    protected ExceptionHandler (Class<T> aException, BiConsumer<T, Context> aHandler) {
        exceptionClass = aException;
        mHandler = aHandler;
    }

    /**
     * Invoked when an exception that is mapped to this handler occurs during routing
     *
     * @param exception The exception that was thrown during routing
     * @param request   The request object providing information about the HTTP request
     * @param response  The response object providing functionality for modifying the response
     */
    public void handle (T exception, Request request, Response response) {
        mHandler.accept (exception, new Context (request, response));
    }

    /**
     * Returns type of exception that this filter will handle
     *
     * @return Type of exception
     */
    public Class<T> exceptionClass() {
        return this.exceptionClass;
    }
}
