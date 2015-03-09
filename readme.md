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

API Docs: [HTTP](http://there4.co/sabina/http/) [Extra](http://there4.co/sabina/extra/)

[JCenter]: https://bintray.com/jamming/maven/Sabina


Getting Started
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

    ./gradle/wrapper javadoc site

The result is put in `/build/docs/javadoc`


The Mission
-----------

The purpose of the project is to provide a micro Web framework with the following priorities (in
order):

* Simple to use
* Easily hackable
* Extensible to different backends
* Be small


IDE Settings
------------

Take care of the output path in IntelliJ, for Gradle projects maybe the tests classes are not
generated in the same place as Gradle itself.


Examples
---------

Check out and try the examples in the source code.

Simple example showing some basic functionality ([SimpleExample.java][Simple])
[Simple]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/examples/SimpleExample.java

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

You can also check the ([integration tests])
[integration tests]: //github.com/jamming/sabina/tree/master/http/src/test/java/sabina/it/undertow


License
-------

Copyright © 2014 Juan José Aguililla. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions
and limitations under the License.
