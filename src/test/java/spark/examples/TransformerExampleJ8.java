package spark.examples;

import static spark.Spark.get;
import static spark.content.JsonContent.toJson;

class TransformerExampleJ8 {
    public static void main (String args[]) {
        get ("/hello", "application/json", it -> toJson (new MyMessage ("Hello World")));
    }
}
