package spark.examples;

class MyMessage {
    private String message;

    MyMessage (String message) {
        this.message = message;
    }

    String getMessage () {
        return message;
    }

    void setMessage (String message) {
        this.message = message;
    }
}
