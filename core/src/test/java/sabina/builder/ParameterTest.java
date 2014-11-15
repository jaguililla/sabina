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

import static org.testng.Assert.*;
import static sabina.builder.Parameter.parameter;
import static sabina.builder.Parameter.parameters;
import static sabina.builder.ParameterName.*;

import java.util.List;

import org.testng.annotations.Test;

public class ParameterTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void parameterWithoutName () throws Exception {
        new Parameter<> (null, 0);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void parameterWithoutValue () throws Exception {
        new Parameter<> (FILES_FOLDER, null);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void parameterWithoutNameNeitherValue () throws Exception {
        new Parameter<> (null, null);
    }

    @Test public void parameterCreation () throws Exception {
        Parameter<String> param = new Parameter<> (FILES_FOLDER, "_");
        assertEquals (param.name, FILES_FOLDER);
        assertEquals (param.value, "_");
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void parametersWithNulls () throws Exception {
        parameters (
            parameter (FILES_FOLDER, "value"),
            null
        );
    }

    @Test public void listOfParameters () throws Exception {
        List<Parameter<?>> params = parameters (
            parameter (FILES_FOLDER, "/"),
            parameter (PORT, 6868)
        );

        assertEquals (params.get (0).name, FILES_FOLDER);
        assertEquals (params.get (0).value, "/");
        assertEquals (params.get (1).name, PORT);
        assertEquals (params.get (1).value, 6868);
    }
}
