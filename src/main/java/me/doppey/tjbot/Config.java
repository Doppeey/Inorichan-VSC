package me.doppey.tjbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {

    private Map<String, String> map = new HashMap<>();

    public Config(Properties properties) {
        properties.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                map.put((String) k, (String) v);
            }
        });
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : value;
    }

    public String getProperty(String key) {
        return map.get(key);
    }
}
