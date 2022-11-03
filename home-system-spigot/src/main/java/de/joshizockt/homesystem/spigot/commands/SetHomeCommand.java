package de.joshizockt.homesystem.spigot.commands;

import de.joshizockt.homesystem.api.HomeSystemAPI;
import de.joshizockt.homesystem.spigot.HomeSystemSpigot;
import de.joshizockt.homesystem.spigot.util.PermissionUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = (Player) sender;
        if(!PermissionUtil.checkPermission(p, PermissionUtil.SET_HOME_PERMISSION)) {
            return false;
        }

        if(args.length >= 1) {
            UUID uuid = p.getUniqueId();
            String name = args[0];
            if(name.length() > 16) {
                p.sendMessage(HomeSystemSpigot.getInstance().getMessage("error.nameTooLong", "16"));
                return false;
            }
            if(HomeSystemAPI.getAPI().getHomeManager().hasHome(uuid, name)) {
                p.sendMessage(HomeSystemSpigot.getInstance().getMessage("error.homeExists"));
                return false;
            }
            Location location = p.getLocation();
            HomeSystemAPI.getAPI().getHomeManager().setHome(p.getUniqueId(), name, location);
            p.sendMessage(HomeSystemSpigot.getInstance().getMessage("command.set.success", name));
            return true;
        } else {
            p.sendMessage(HomeSystemSpigot.getInstance().getMessage("command.set.usage"));
        }

        return false;
    }

}
