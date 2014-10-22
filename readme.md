[![Build Img]][Build Status] [![Coverage Img]][Coverage Status]

[Build Img]: https://travis-ci.org/jamming/sabina.svg?branch=master
[Build Status]: https://travis-ci.org/jamming/sabina

[Coverage Img]: https://img.shields.io/coveralls/jamming/sabina.svg
[Coverage Status]: https://coveralls.io/r/jamming/sabina


Sabina - a Sinatra inspired web framework
=========================================

Sabina 2.0.1 is now available on [Bintray]!!! (TODO Add JCenter link)

```xml
<dependency>
  <groupId>com.sabina</groupId>
  <artifactId>sabina</artifactId>
  <version>1.0.0</version>
</dependency>
```

API Docs: http://there4.co/sabina (TODO)

[Bintray]: https://bintray.com/jamming/maven/Sabina


Getting started
---------------

```java
import static sabina.Sabina.*;

public class HelloWorld {
   public static void main (String[] args) {
      serve (
          get ("/hello", it -> "Hello World!");
      )
   }
}
```

View at: http://localhost:4567/hello

More documentation is on the way!

You can also check out the javadoc. After getting the source from github run: 

    ./gradlew javadoc

The result is put in `/build/docs/javadoc`


Examples
---------

Check out and try the examples in the source code.

Simple example showing some basic functionality
([SimpleExample.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/SimpleExample.java))

A simple CRUD example showing howto create, get, update and delete book resources
([Books.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/Books.java))

Example showing a very simple (and stupid) authentication filter that is executed before all
other resources
([FilterExample.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/FilterExample.java))

Example showing how to use attributes
([FilterExampleAttributes.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/FilterExampleAttributes.java))

Example showing how to serve static resources
([StaticResources.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/StaticResources.java))

Example showing how to define content depending on accept type
([JsonAcceptTypeExample.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/JsonAcceptTypeExample.java))

Example showing how to render a view from a template
([FreeMarkerExample.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/FreeMarkerExample.java))

Example of using Transformer.
([TransformerExample.java](//github.com/jamming/sabina/tree/master/core/src/test/java/sabina/examples/TransformerExample.java))


TODO
----

* Add optimize 'profile'
* Remove duplicated tests. Ie: postOk (JUnit test method)
* Move tests to integration tests
* Create unit tests
* Integrate with:
  * http://jackson.codehaus.org
  * http://metrics.codahale.com
  * http://www.jdbi.org
  * http://flywaydb.org
  * http://pholser.github.io/jopt-simple (expose a common set of options for all microservices)
* Use https://huboard.com
* Copy Javadoc to site prior to publishing
* Helper for multiline strings
* Helper for JDBC
* Integrate Redis
* Add Travis links, huboard, issues, etc.
* Example project (command to fetch and start, deployment heroku, building executable WAR)
* Start child processes (clones) of the microservice
* Manage versions
* Deploy GH pages in Travis: after_success: ./gradlew cobertura coveralls jbake publishGhPages
* Fix documentation site styles and pages
* Deploy releases into Bintray
* Clean code:
  * Remove Utils
  * Replace MimeParse with JDKs javax.activation.MimetypesFileTypeMap
