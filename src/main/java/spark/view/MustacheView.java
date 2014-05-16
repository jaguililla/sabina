package spark.view;

import java.util.HashMap;
import java.util.Map;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

/**
 * Created by jam on 16/05/14.
 */
public class MustacheView {
    public static String renderMustache (String aName, Object aModel) {
        String text = "One, two, {{three}}. Three sir!";
        Template tmpl = Mustache.compiler ().compile (text);
        Map<String, String> data = new HashMap<String, String> ();
        data.put ("three", "five");
        return tmpl.execute (aModel);
    }
}
