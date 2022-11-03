package de.joshizockt.homesystem.spigot.gui;

import de.joshizockt.homesystem.api.HomeSystemAPI;
import de.joshizockt.homesystem.api.manager.HomeManager;
import de.joshizockt.homesystem.common.util.Configuration;
import de.joshizockt.homesystem.spigot.HomeSystemSpigot;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HomesGUI implements Listener {


    public static final NamespacedKey CLICK_EVENT_KEY = new NamespacedKey(HomeSystemSpigot.getInstance(), "clickEvent");

    private final Configuration configuration;
    private final String title;

    private final ItemStack REMOVE_ALL_HOMES_ITEM = new SkullBuilder().setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=")
            .setDisplayName("§cAlle Homes löschen")
            .addData(ItemBuilder.DRAGABLE_KEY, "false")
            .addData(CLICK_EVENT_KEY, "homesDeleteAll")
            .build();

    private final Material HOME_ITEM = Material.PAPER;

    public HomesGUI(Configuration configuration) {
        this.configuration = configuration;
        title = ChatColor.translateAlternateColorCodes('&', configuration.getString("gui.title", "&aDeine Homes"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equals(title)) return;
        e.setCancelled(true);

        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getType() == Material.AIR) return;
        if(e.getCurrentItem().getItemMeta() == null) return;

        if(!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        ItemStack i = e.getCurrentItem();
        if(!i.hasItemMeta()) return;
        ItemMeta im = i.getItemMeta();
        PersistentDataContainer container = im.getPersistentDataContainer();
        if(container.has(ItemBuilder.DRAGABLE_KEY)) {
            String dragable = container.get(ItemBuilder.DRAGABLE_KEY, PersistentDataType.STRING);
            if(dragable != null && dragable.equals("false")) {
                e.setCancelled(true);
            }
        }
        if(container.has(CLICK_EVENT_KEY)) {
            String clickEvent = container.get(CLICK_EVENT_KEY, PersistentDataType.STRING);
            if(clickEvent != null) {
                if(clickEvent.equals("homesDeleteAll")) {
                    //TODO: Delete all homes
                    HomeSystemAPI.getAPI().getHomeManager().deleteAll(p.getUniqueId());
                    p.closeInventory();
                    p.sendMessage(HomeSystemSpigot.getInstance().getMessage("allDeleted"));
                } else if(clickEvent.startsWith("homeTeleport:")) {
                    String homeName = clickEvent.replaceFirst("homeTeleport:", "");
                    HomeSystemAPI api = HomeSystemAPI.getAPI();
                    HomeManager manager = api.getHomeManager();
                    if(manager.hasHome(p.getUniqueId(), homeName)) {
                        Location location = manager.getHome(p.getUniqueId(), homeName);
                        if(location != null) {
                            p.teleport(location);
                            p.closeInventory();
                            p.sendMessage(HomeSystemSpigot.getInstance().getMessage("command.tp.success", homeName));
                        }
                    }
                }
            }
        }

    }

    public void open(Player player) {

        Inventory inv = Bukkit.createInventory(player, 6*9, title);

        String[] lore = Arrays.stream(configuration.getStringArray("gui.item.lore"))
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
        List<String> homes = HomeSystemAPI.getAPI().getHomeManager().getAll(player.getUniqueId()).keySet().stream().toList();
        for (String home : homes) {
            inv.addItem(
                    new ItemBuilder(HOME_ITEM)
                            .setDisplayName("§e" + home)
                            .setLoreArray(lore)
                            .addData(CLICK_EVENT_KEY, "homeTeleport:" + home)
                            .addData(ItemBuilder.DRAGABLE_KEY, "false")
                            .build()
            );
        }

        // Last Row, fill with BLACK_STAINED_GLASS_PANE and set the REMOVE_ALL_HOMES_ITEM in the middle
        for(int i = 45; i < 54; i++) {
            if(i == 49) inv.setItem(i, REMOVE_ALL_HOMES_ITEM);
            else inv.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).addData(ItemBuilder.DRAGABLE_KEY, "false").setDisplayName(" ").build());
        }

        player.openInventory(inv);

    }

}
