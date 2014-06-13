package spark.examples;

import static java.lang.System.out;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;

import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

    @Override public void init () {
        out.println ("HELLO J8!!!");

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/hi", it -> "Hello World!");

        get ("/:param", it -> "echo: " + it.params (":param"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        after ("/hi", it -> it.header ("after", "foobar"));
    }
}
