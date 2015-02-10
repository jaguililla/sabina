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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.BiConsumer;

public final class Fault<T extends Exception> {
    private final BiConsumer<T, Request> handler;

    /**
     * Initializes the filter with the provided exception type
     */
    Fault (final BiConsumer<T, Request> handler) {
        checkArgument (handler != null);

        this.handler = handler;
    }

    /**
     * Invoked when an exception that is mapped to this handler occurs during routing
     *
     * @param exception The exception that was thrown during routing
     * @param request The request object providing information about the HTTP request
     */
    void handle (final T exception, final Request request) {
        handler.accept (exception, request);
    }
}
