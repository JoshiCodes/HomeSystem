package de.joshizockt.homesystem.spigot.commands;

import de.joshizockt.homesystem.api.HomeSystemAPI;
import de.joshizockt.homesystem.spigot.HomeSystemSpigot;
import de.joshizockt.homesystem.spigot.util.PermissionUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DelHomeCommand implements CommandExecutor, @Nullable TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = (Player) sender;
        if(!PermissionUtil.checkPermission(p, PermissionUtil.DEL_HOME_PERMISSION)) {
            return false;
        }

        if(args.length >= 1) {
            UUID uuid = p.getUniqueId();
            String name = args[0];
            if(!HomeSystemAPI.getAPI().getHomeManager().hasHome(uuid, name)) {
                p.sendMessage(HomeSystemSpigot.getInstance().getMessage("error.noHome", name));
                return false;
            }
            HomeSystemAPI.getAPI().getHomeManager().deleteHome(p.getUniqueId(), name);
            p.sendMessage(HomeSystemSpigot.getInstance().getMessage("command.del.success", name));
            return true;
        } else {
            p.sendMessage(HomeSystemSpigot.getInstance().getMessage("command.del.usage"));
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) return null;
        List<String> homes = HomeSystemAPI.getAPI().getHomeManager().getAll(((Player) commandSender).getUniqueId()).keySet().stream().toList();
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], homes, completions);
        Collections.sort(completions);
        return completions;
    }

}
