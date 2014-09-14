/*
 * Copyright © 2014 Juan José Aguililla. All rights reserved.
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

package sabina.builder;

import static java.util.Arrays.asList;

import java.util.List;

/**
 * TODO .
 *
 * @author jam
 */
public class Parameter<T> {
    public final ParameterName name;
    public final T value;

    public static List<Parameter<?>> parameters (Parameter... parameters) {
        return asList (parameters);
    }

    public static <P> Parameter<P> parameter (ParameterName name, P value) {
        return new Parameter (name, value);
    }

    public Parameter (ParameterName name, T value) {
        this.name = name;
        this.value = value;
    }
}
