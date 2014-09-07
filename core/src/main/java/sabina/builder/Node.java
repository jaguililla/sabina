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

public abstract class Node {
    public Node parent;
    public final List<Node> children;

    Node (Node... aChildren) {
        children = asList (aChildren);
        for (Node n : children)
            n.parent = this;
    }
}

// TODO Integrate in Server
//
//    public Server (Node... actions) {
//        buildActions (actions).forEach (this::addRoute);
//    }
//
//    private List<Action> buildActions (Node... actions) {
//        return null;
//    }
//
//    static void getActions (final List<Action> rules, final Node root) {
//        for (Node n : root.children)
//            if (n.children.isEmpty ())
//                rules.add (((MethodNode)n).getRule ());
//            else
//                getActions (rules, n);
//    }
//
//    static List<Action> getActions (final Node root) {
//        ArrayList<Action> rules = new ArrayList<> ();
//        getActions (rules, root);
//
//        if (LOG.isLoggable (INFO))
//            for (Action r : rules)
//                LOG.info ("Rule for " + r.method + " " + r.path + " (" + r.contentType + ")");
//
//        return rules;
//    }
//
//    Action getAction () {
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

