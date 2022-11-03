package de.joshizockt.homesystem.spigot;

import de.joshizockt.homesystem.api.HomeSystemAPI;
import de.joshizockt.homesystem.common.util.Configuration;
import de.joshizockt.homesystem.common.util.SQLConnector;
import de.joshizockt.homesystem.spigot.commands.DelHomeCommand;
import de.joshizockt.homesystem.spigot.commands.HomeCommand;
import de.joshizockt.homesystem.spigot.commands.ListHomesCommand;
import de.joshizockt.homesystem.spigot.commands.SetHomeCommand;
import de.joshizockt.homesystem.spigot.util.PermissionUtil;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class HomeSystemSpigot extends JavaPlugin {

    public static String PREFIX;

    private static HomeSystemSpigot instance;

    private Configuration config;
    private SQLConnector connector;
    private HomeSystemAPI api;

    @Override
    public void onEnable() {
        instance = this;

        config = new Configuration("config.json", this);
        config.copyDefaults();

        PREFIX = ChatColor.translateAlternateColorCodes('&', config.getString("messages.prefix", ""));

        try {
            this.connector = new SQLConnector(
                    config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.database"),
                    config.getString("mysql.user"),
                    config.getString("mysql.password")
            );
            connector.createDefaults();
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Could not connect to MySQL Database! Disabling Plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        api = new HomeSystemAPI(this, connector);

        PermissionUtil.init(config);

        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("delhome").setExecutor(new DelHomeCommand());
        getCommand("delhome").setTabCompleter(new DelHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("home").setTabCompleter(new HomeCommand());
        getCommand("homes").setExecutor(new ListHomesCommand(this));

    }

    @Override
    public void onDisable() {

    }

    public Configuration getConfiguration() {
        return this.config;
    }

    public String getMessage(String key, String... replacements) {
        String str = config.getString("messages." + key);
        for(int i = 0; i < replacements.length; i++) {
            str = str.replaceAll("%" + i + "%", replacements[i]);
        }
        return PREFIX + ChatColor.translateAlternateColorCodes('&', str);
    }

    public static HomeSystemSpigot getInstance() {
        return instance;
    }

}
