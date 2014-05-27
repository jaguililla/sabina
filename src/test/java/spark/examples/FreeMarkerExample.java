package spark.examples;

import static spark.Spark.get;
import static spark.view.FreeMarkerView.renderFreeMarker;

import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

class FreeMarkerExample {
    public static void main (String args[]) {
        get (new Route ("/hello") {
            @Override public Object handle (Request request, Response response) {
                Map<String, Object> attributes = new HashMap<> ();
                attributes.put ("message", "Hello World");

                // The hello.ftl file is located in directory:
                // src/test/resources/spark/examples/templateview/freemarker
                return renderFreeMarker ("hello.ftl", attributes);
            }
        });
    }
}
