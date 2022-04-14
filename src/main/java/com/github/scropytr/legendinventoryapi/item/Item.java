package com.github.scropytr.legendinventoryapi.item;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public class Item {

    @Setter
    private ItemStack itemStack;

    @Setter
    private Consumer<InventoryClickEvent> clickAction;

    private final boolean supports = XMaterial.supports(16);

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item(XMaterial material) {
        this.itemStack = material.parseItem();
    }

    public Item withListener(Consumer<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    public Item addEnchantment(Enchantment enchantment, int level) {
        return change(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public Item removeEnchantment(Enchantment enchantment) {
        return change(itemStack -> itemStack.removeEnchantment(enchantment));
    }

    public Item setDamage(int damage) {
        return change(itemStack -> itemStack.setDurability((short) (itemStack.getType().getMaxDurability() - damage)));
    }

    public Item setDisplayName(String displayName) {
        return changeMeta(itemMeta -> itemMeta.setDisplayName(displayName));
    }

    public Item setType(XMaterial material) {
        return change(itemStack -> itemStack.setType(material.parseMaterial()));
    }

    public Item setAmount(int amount) {
        return change(itemStack -> itemStack.setAmount(amount));
    }

    public Item glowing(boolean glowing) {
        if (glowing)
            return addEnchantment(Enchantment.LUCK, 1).changeMeta(im -> im.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        return this;
    }

    public Item glowing() {
        return glowing(true);
    }

    public Item setLore(List<String> lore) {
        return changeMeta(itemStack -> itemStack.setLore(lore));
    }

    public Item addLore(String line) {
        if (line == null) return this;
        List<String> lore = itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore() ?
                new ArrayList<>(itemStack.getItemMeta().getLore()) : new ArrayList<>();
        lore.add(line);
        return setLore(lore);
    }

    public Item changeMeta(Consumer<? super ItemMeta> consumer) {
        return change(itemStack -> {
            ItemMeta meta = itemStack.getItemMeta();
            consumer.accept(meta);
            itemStack.setItemMeta(meta);
        });
    }

    public Item change(Consumer<? super ItemStack> consumer) {
        consumer.accept(itemStack);
        return this;
    }

    public Item setHeadData(String headData) {
        if (!(itemStack.getType() == XMaterial.PLAYER_HEAD.parseMaterial())) return this;
        if (headData == null) return this;

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound skull = nbtItem.addCompound("SkullOwner");
        if (supports) {
            skull.setUUID("Id", UUID.randomUUID());
        } else {
            skull.setString("Id", UUID.randomUUID().toString());
        }

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", headData);
        itemStack = nbtItem.getItem();
        return this;
    }

}
