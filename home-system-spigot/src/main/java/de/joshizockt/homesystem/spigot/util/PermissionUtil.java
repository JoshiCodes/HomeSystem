package de.joshizockt.homesystem.spigot.util;

import de.joshizockt.homesystem.common.util.Configuration;
import de.joshizockt.homesystem.spigot.HomeSystemSpigot;
import org.bukkit.entity.Player;

public class PermissionUtil {

    public static String SET_HOME_PERMISSION;
    public static String DEL_HOME_PERMISSION;
    public static String HOME_PERMISSION;
    public static String LIST_HOMES_PERMISSION;

    public static String ADMIN_PERMISSION;

    public static void init(Configuration configuration) {

        SET_HOME_PERMISSION = configuration.getString("permissions.set");
        DEL_HOME_PERMISSION = configuration.getString("permissions.del");
        HOME_PERMISSION = configuration.getString("permissions.tp");
        LIST_HOMES_PERMISSION = configuration.getString("permissions.list");

        ADMIN_PERMISSION = configuration.getString("permissions.all");

    }

    public static boolean checkPermission(Player p, String permission) {
        if(p.hasPermission(permission) || p.hasPermission(ADMIN_PERMISSION)) {
            return true;
        }
        p.sendMessage(HomeSystemSpigot.getInstance().getMessage("error.noPermission"));
        return false;
    }

}
