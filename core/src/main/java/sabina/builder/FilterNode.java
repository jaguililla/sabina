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

import java.util.function.Consumer;

import sabina.Context;
import sabina.HttpMethod;

/**
 * TODO .
 *
 * @author jam
 */
public class FilterNode extends MethodNode {
    public final Consumer<Context> handler;

    public FilterNode (HttpMethod aMethod, Consumer<Context> aHandler) {
        super (aMethod);

        if (aHandler == null)
            throw new IllegalArgumentException ();

        handler = aHandler;
    }
//
//    Rule getRule () {
//        String aContentType = "";
//        String aPath = "";
//
//        for (Node p = parent; p != null; p = p.parent)
//            if (p instanceof PathNode)
//                aPath = ((PathNode)p).path + aPath;
//            else if (p instanceof ContentTypeNode)
//                aContentType += ((ContentTypeNode)p).contentType;
//            else
//                throw new IllegalStateException ("Unsupported node type");
//
//        return new Rule (handler, method, aContentType, aPath);
//    }
}
