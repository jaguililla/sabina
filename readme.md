[![Build Status](https://travis-ci.org/jamming/spark.svg?branch=master)][status]
[status]: https://travis-ci.org/jamming/spark


Sabina - a Sinatra inspired web framework
=========================================

Sabina 1.0.0 is now available on Bintray!!!

```xml
<dependency>
  <groupId>com.sabina</groupId>
  <artifactId>sabina</artifactId>
  <version>1.0.0</version>
</dependency>
```

API Docs: [http://there4.co/spark]


Getting started
---------------

```java
import static sabina.Sabina.*;

public class HelloWorld {
   public static void main (String[] args) {
      get ("/hello", it -> "Hello World!");
   }
}
```

View at: [http://localhost:4567/hello]

More documentation is on the way!

You can also check out the javadoc. After getting the source from github run: 

    ./gradlew javadoc

The result is put in `/build/docs/javadoc`


Examples
---------

Check out and try the examples in the source code.

Simple example showing some basic functionality
([SimpleExample.java](tree/master/src/test/java/spark/examples/SimpleExample.java))
([SimpleExample.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/SimpleExample.java))

A simple CRUD example showing howto create, get, update and delete book resources
([Books.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/Books.java))

Example showing a very simple (and stupid) authentication filter that is executed before all
other resources
([FilterExample.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/FilterExample.java))

Example showing how to use attributes
([FilterExampleAttributes.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/FilterExampleAttributes.java))

Example showing how to serve static resources
([StaticResources.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/StaticResources.java))

Example showing how to define content depending on accept type
([JsonAcceptTypeExample.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/JsonAcceptTypeExample.java))

Example showing how to render a view from a template
([FreeMarkerExample.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/FreeMarkerExample.java))

Example of using Transformer.
([TransformerExample.java](//github.com/jamming/spark/tree/master/src/test/java/spark/examples/TransformerExample.java))


TODO
----

* Add license to this readme (and to the site)
* Add source url in the site (scm:git:git@github.com:perwendel/spark.git)
* Add optimize 'profile'
* Remove duplicated tests. Ie: postOk (JUnit test method)
* Move tests to integration tests
* Create unit tests
