package de.joshizockt.homesystem.spigot.commands;

import de.joshizockt.homesystem.spigot.HomeSystemSpigot;
import de.joshizockt.homesystem.spigot.gui.HomesGUI;
import de.joshizockt.homesystem.spigot.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListHomesCommand implements CommandExecutor {

    private HomesGUI gui;

    public ListHomesCommand(HomeSystemSpigot plugin) {
        gui = new HomesGUI(plugin.getConfiguration());
        Bukkit.getPluginManager().registerEvents(gui, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = (Player) sender;
        if(!PermissionUtil.checkPermission(p, PermissionUtil.SET_HOME_PERMISSION)) {
            return false;
        }

        gui.open(p);
        return true;
    }

}
