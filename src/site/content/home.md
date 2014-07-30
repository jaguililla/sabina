title=About
date=2013-09-24
type=page
status=published
~~~~~~


News
----

Spark 2.0.0 re-written for Java 8 and Lambdas available on Maven central!


Quick start
-----------

Add the Spark maven dependency and you're ready to go:

```java
import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", it -> "Hello World");
    }
}
```

Ignite and view at
------------------

`http://localhost:4567/hello`

