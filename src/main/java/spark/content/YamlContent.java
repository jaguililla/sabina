package spark.content;

import org.yaml.snakeyaml.Yaml;

public class YamlContent {
    private static Yaml yaml = new Yaml ();

    public static String toJson (Object model) {
        return yaml.dump (model);
    }
}
