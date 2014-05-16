package spark.servlet;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;

public class MyAppJ8 implements SparkApplication {

    @Override public void init () {
        System.out.println ("HELLO J8!!!");

        before ("/j8/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/j8/hi", it -> "Hello World!");

        get ("/j8/:param", it -> "echo: " + it.params (":param"));

        get ("/j8/", it -> "Hello Root!");

        post ("/j8/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        after ("/j8/hi", it -> it.header ("after", "foobar"));

        try {
            Thread.sleep (500);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
