title=About
date=2013-09-24
type=page
status=published
~~~~~~


News
----

Sabina 2.0.0 re-written for Java 8 and Lambdas available on Bintray and Maven central!


Quick start
-----------

Add the Sabina maven dependency and you're ready to go:

```java
import static sabina.Sabina.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", it -> "Hello World");
    }
}
```

Ignite and view at
------------------

`http://localhost:4567/hello`

