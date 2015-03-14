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

package sabina.content;

import static javax.xml.bind.Marshaller.*;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * .
 */
public class XmlContent {
    /**
     * .
     * @param model .
     * @return .
     * @throws JAXBException
     */
    public static String toXml (Object model) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(model.getClass());
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty (JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty (JAXB_FRAGMENT, true);
        StringWriter writer = new StringWriter ();
        marshaller.marshal (model, writer);

        return writer.toString ();
    }
}
