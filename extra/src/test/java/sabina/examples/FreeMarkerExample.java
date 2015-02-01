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

package sabina.examples;

import static sabina.Sabina.*;
import static sabina.view.FreeMarkerView.renderFreeMarker;

import java.util.HashMap;
import java.util.Map;

class FreeMarkerExample {
    public static void main (String args[]) {
        get ("/hello", it -> {
            Map<String, Object> attributes = new HashMap<> ();
            attributes.put ("message", "Hello World");

            // The hello.ftl file is located in directory: src/test/resources/sabina/view
            return renderFreeMarker ("hello.ftl", attributes);
        });

        start ();
    }
}