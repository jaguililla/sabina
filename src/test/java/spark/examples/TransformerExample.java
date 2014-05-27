package spark.examples;

import static spark.Spark.get;
import static spark.content.JsonContent.toJson;

import spark.Request;
import spark.Response;
import spark.Route;

class TransformerExample {
    public static void main (String args[]) {
        get (new Route ("/hello", "application/json") {
            @Override public Object handle (Request request, Response response) {
                return toJson (new MyMessage ("Hello World"));
            }
        });
    }
}
