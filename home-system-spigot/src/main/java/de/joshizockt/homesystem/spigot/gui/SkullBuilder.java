package de.joshizockt.homesystem.spigot.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullBuilder extends ItemBuilder {

    private String owner;
    private String texture;

    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    public SkullBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public SkullBuilder setTexture(String texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public ItemStack build() {
        ItemStack i = super.build();
        i.setDurability((short) 3);
        SkullMeta im = (SkullMeta) i.getItemMeta();
        if(owner != null) im.setOwner(owner);
        else if(texture != null) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
            profile.setProperty(new ProfileProperty("textures", texture));
            im.setPlayerProfile(profile);
        }
        i.setItemMeta(im);
        return i;
    }

}
