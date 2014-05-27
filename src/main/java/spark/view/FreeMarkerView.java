package spark.view;

import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerView {
    private static Configuration configuration = createFreemarkerConfiguration ();

    private static Configuration createFreemarkerConfiguration () {
        Configuration retVal = new Configuration ();
        retVal.setClassForTemplateLoading (FreeMarkerView.class, "freemarker");
        return retVal;
    }

    public static String renderFreeMarker (String viewName, Object model) {
        try {
            StringWriter stringWriter = new StringWriter ();

            Template template = configuration.getTemplate (viewName);
            template.process (model, stringWriter);

            return stringWriter.toString ();
        }
        catch (IOException | TemplateException e) {
            throw new IllegalArgumentException (e);
        }
    }
}
