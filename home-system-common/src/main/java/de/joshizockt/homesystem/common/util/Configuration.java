package de.joshizockt.homesystem.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JSON Configuration File
 * All values are expected to be of the type String
 *
 */
public class Configuration {

    private final JavaPlugin plugin;

    private final File file;

    private JsonObject json;

    public Configuration(String filename, JavaPlugin plugin) {
        this(new File(plugin.getDataFolder(), filename), plugin);
    }

    public Configuration(File file, JavaPlugin plugin) {
        this.file = file;
        this.plugin = plugin;

        if(file.exists()) update();

    }

    /**
     * Copies the default configuration from the resources to the data folder of the Plugin
     * Does nothing if the configuration File already exists or no default configuration is provided
     * @return true if the default configuration was copied, false if not
     */
    public boolean copyDefaults() {
        if (!file.exists()) {
            plugin.saveResource(file.getName(), false);
            update();
            return true;
        }
        return false;
    }

    /**
     * Get a String Object from the Configuration
     * @param key The key of the String, use "." to get a value from a sub-configuration
     * @param def The fallback Value used if the key is not found
     * @return The String assigned to the key or def if the key is not found
     */
    public String getString(String key, String def) {
        String str = getString(key);
        if(str == null) return def;
        return str;
    }

    /**
     * Get a String Object from the Configuration
     * @param key The key of the String, use "." to get a value from a sub-configuration
     * @return The String assigned to the key or null if the key is not found
     */
    public String getString(String key) {
        JsonElement element = get(key);
        if(element == null) return null;
        if(element.isJsonPrimitive()) return element.getAsString();
        else return element.toString();
    }

    public JsonElement get(String key) {
        if(json == null) update();
        String[] keys = key.split("\\.");
        key = keys[0];
        JsonElement element = json.get(key);
        for(int i = 1; i < keys.length; i++) {
            String k = keys[i];
            if(element == null || element.isJsonNull()) {
                return null;
            }
            if(element.isJsonObject()) {
                element = element.getAsJsonObject().get(k);
            }
        }
        return element;
    }

    public void update() {
        try {
            FileInputStream in = new FileInputStream(getFile());
            InputStreamReader reader = new InputStreamReader(in);
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return file;
    }

    public String[] getStringArray(String s) {
        List<String> list = getStringList(s);
        return list.toArray(new String[0]);
    }

    public List<String> getStringList(String key) {
        JsonElement element = get(key);
        if(element == null) return null;
        if(element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<String> list = new java.util.ArrayList<>();
            for(JsonElement e : array) {
                list.add(e.getAsString());
            }
            return list;
        }
        return new ArrayList<>();
    }

}
