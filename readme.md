[![Build Img]][Build Status] [![Coverage Img]][Coverage Status]

[Build Img]: https://travis-ci.org/jamming/sabina.svg?branch=master
[Build Status]: https://travis-ci.org/jamming/sabina

[Coverage Img]: https://img.shields.io/coveralls/jamming/sabina.svg
[Coverage Status]: https://coveralls.io/r/jamming/sabina


Sabina - a Sinatra inspired web framework
=========================================

Sabina 1.0.0 is now available on [JCenter]!!!

```groovy
dependencies {
  compile 'sabina:http:1.0.0'
}
```

```xml
<dependency>
  <groupId>sabina</groupId>
  <artifactId>http</artifactId>
  <version>1.0.0</version>
</dependency>
```

API Docs: [Core](http://there4.co/sabina/http/) [Extra](http://there4.co/sabina/http/)

[JCenter]: TODO Add JCenter link


Getting started
---------------

```java
import static sabina.Sabina.*;

public class HiWorld {
    public static void main (String[] args) {
        get ("/hello", it -> "Hi World!").start ();
    }
}
```

View at: http://localhost:4567/hello

You can also check out the javadoc. After getting the source from github run:

    ./gradlew javadoc site

The result is put in `/build/docs/javadoc`


IDE Settings
------------

Take care of the output path in IntelliJ, for Gradle projects maybe the tests classes are not
generated in the same place as Gradle itself.


Examples
---------

Check out and try the examples in the source code.

Simple example showing some basic functionality ([SimpleExample.java][Simple])
[Simple]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/SimpleExample.java

A simple CRUD example showing howto create, get, update and delete book resources ([Books.java][Books])
[Books]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/Books.java

Example showing a very simple (and stupid) authentication filter that is executed before all
other resources ([FilterExample.java][Filter])
[Filter]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/FilterExample.java

Example showing how to use attributes ([FilterExampleAttributes.java][FilterAttributes])
[FilterAttributes]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/FilterExampleAttributes.java

Example showing how to serve static resources ([StaticResources.java][StaticResources])
[StaticResources]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/StaticResources.java

Example showing how to define content depending on accept type ([JsonAcceptTypeExample.java][JsonAcceptType])
[JsonAcceptType]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/JsonAcceptTypeExample.java

Example showing how to render a view from a template ([FreeMarkerExample.java][FreeMarker])
[FreeMarker]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/FreeMarkerExample.java

Example of using Transformer. ([TransformerExample.java][Transformer])
[Transformer]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/TransformerExample.java


LICENSE
-------

Copyright © 2014 Juan José Aguililla. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions
and limitations under the License.


TODO
----

* Add social buttons to site
* Fix documentation site styles and pages
* Add Travis links, huboard, issues, etc. (in reference documentation)

* Add optimize 'profile'

* Integrate with:
  * http://jackson.codehaus.org
  * http://metrics.codahale.com
  * http://www.jdbi.org
  * http://flywaydb.org
  * http://pholser.github.io/jopt-simple (expose a common set of options for all microservices)
  * http://redis.io
* Helper for multiline strings
* Helper for JDBC
* Swagger code generator

* Example project (command to fetch and start, deployment heroku, building executable WAR)
* Deploy GH pages in Travis: after_success: ./gradlew cobertura coveralls jbake publishGhPages
