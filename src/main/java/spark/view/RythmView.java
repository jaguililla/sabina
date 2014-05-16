package spark.view;

import org.rythmengine.Rythm;

public class RythmView {
    public static String renderMustache (String aName, Object aModel) {
        return Rythm.render (aName, aModel);
    }
}
