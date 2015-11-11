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

package sabina.servlet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.testng.annotations.Test;
import sabina.backend.BackendFactory;

@Test public class BackendFactoryTest {
    @Test (expectedExceptions = IllegalStateException.class)
    public void backend_factory_cannot_be_instantiated () throws Throwable {
        try {
            Constructor<BackendFactory> constructor = BackendFactory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace ();
        }
        catch (InvocationTargetException e) {
            throw e.getCause ();
        }
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void create_invalid_backend_results_in_exception () {
        BackendFactory.create ("bad", null, false);
    }
}
