package spark.examples;

import static spark.Spark.get;
import static spark.content.JsonContent.toJson;

class MyMessage {
    private String message;

    MyMessage (String message) {
        this.message = message;
    }

    String getMessage () { return message; }
    void setMessage (String message) { this.message = message; }
}

class TransformerExample {
    public static void main (String args[]) {
        get ("/hello", "application/json", it -> toJson (new MyMessage ("Hello World")));
    }
}
