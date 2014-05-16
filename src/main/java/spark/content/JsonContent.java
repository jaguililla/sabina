package spark.content;

import com.google.gson.Gson;

public class JsonContent {
    private static Gson gson = new Gson ();

    public static String toJson (Object model) {
        return gson.toJson (model);
    }
}
