package de.joshizockt.homesystem.spigot.gui;

import de.joshizockt.homesystem.spigot.HomeSystemSpigot;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.naming.Name;
import java.util.Arrays;
import java.util.HashMap;

public class ItemBuilder extends ItemStack {

    public static final NamespacedKey DRAGABLE_KEY = new NamespacedKey(HomeSystemSpigot.getInstance(), "dragable");

    private String displayName;
    private String[] lore;

    private HashMap<NamespacedKey, String> nbtData;

    public ItemBuilder(Material material) {
        super(material);
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setLoreArray(String... lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder addData(String key, String value) {
        if(nbtData == null) {
            nbtData = new HashMap<>();
        }
        nbtData.put(new NamespacedKey(HomeSystemSpigot.getInstance(), key), value);
        return this;
    }

    public ItemBuilder addData(NamespacedKey key, String value) {
        if(nbtData == null) {
            nbtData = new HashMap<>();
        }
        nbtData.put(key, value);
        return this;
    }

    public ItemStack build() {
        ItemStack i = this;
        ItemMeta im = i.getItemMeta();
        if(displayName != null) im.setDisplayName(displayName);
        if(lore != null) im.setLore(Arrays.asList(lore));

        if(nbtData != null) {
            PersistentDataContainer container = im.getPersistentDataContainer();
            for(NamespacedKey key : nbtData.keySet()) {
                container.set(key, PersistentDataType.STRING, nbtData.get(key));
            }
        }

        i.setItemMeta(im);
        return i;
    }

}
