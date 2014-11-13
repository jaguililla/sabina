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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class NodeTest {
    @Test (expectedExceptions = IllegalArgumentException.class) public void nodeNull () {
        new Node (null) {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void nodeNullChildren () {
        new Node (null, null) {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class) public void nodeNestedNull () {
        new Node (new Node (null) {}) {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void nodeNestedNullChildren () {
        new Node (new Node (null, null) {}) {};
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void nodeNestedNullWithChildren () {
        new Node (new Node () {}, null) {};
    }

    @Test public void node () {
        Node n = new Node () {};
        assertTrue (n.parent == null && n.children.isEmpty ());
    }

    @Test public void nodeOneChild () {
        Node n = new Node (new Node () {}) {};
        assertTrue (n.parent == null && n.children.get (0).parent == n);
    }

    @Test public void nodeTwoChildren () {
        Node n = new Node (new Node () {}, new Node () {}) {};
        assertTrue (
            n.parent == null
            && n.children.get (0).parent == n
            && n.children.get (1).parent == n);
    }

    @Test public void nodeTwoLevelChildren () {
        Node n = new Node (new Node (new Node () {}) {}) {};
        Node child = n.children.get (0);
        Node grandchild = child.children.get (0);
        assertTrue (
            n.parent == null
            && child.parent == n
            && grandchild.parent == child);
    }
}
