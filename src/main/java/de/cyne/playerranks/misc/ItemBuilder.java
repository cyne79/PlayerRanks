package de.cyne.playerranks.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder extends ItemStack {

    private ItemMeta meta;

    public ItemBuilder(Material material, int amount, short durability) {
        this.setType(material);
        this.setAmount(amount);
        this.setDurability(durability);
        this.meta = this.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, (short) 0);
    }

    public ItemBuilder(Material material) {
        this(material, 1, (short) 0);
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.meta.setDisplayName(displayName);
        return this.build();
    }

    public ItemBuilder setLore(String... lore) {
        this.meta.setLore(Arrays.asList(lore));
        return this.build();
    }

    public ItemBuilder addItemFlags(ItemFlag... flag) {
        this.meta.addItemFlags(flag);
        return this.build();
    }

    public ItemBuilder setLore(List<String> lore) {
        for (int i = 0; i < lore.size(); i++)
            lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
        this.meta.setLore(lore);
        return this.build();
    }

    public ItemBuilder setSkullOwner(String texture) {
        try {
            SkullMeta skullMeta = (SkullMeta) this.meta;
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, profile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.build();
    }

    public ItemBuilder build() {
        this.setItemMeta(this.meta);
        return this;
    }

}