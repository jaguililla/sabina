
BALI
====

Helpers for Java. Only general purpose utilities will be added. This library includes:

* Code to initialize collections easily (`Builders.java`).
* Utilities to avoid dealing with checked exceptions (`Unchecked*.java`)
* Other utilities like Key Store handling, CSV utilities or String helpers.

It is mostly composed of static utility methods grouped in interfaces. Interfaces are not meant to
be implemented, they are used over classes only for convenience.

Usage examples can be found in the tests' code: `src/test/groovy`.

## Notice

Part of the AWS test suite is included.

AWS Signature Version 4 Test Suite
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

http://www.apache.org/licenses/LICENSE-2.0.txt

## TODO

* Helpers for map entries that holds other maps or entries (with varargs).
* Create `getFirstKey(String... keys)`
* Code `Strings.tr`
