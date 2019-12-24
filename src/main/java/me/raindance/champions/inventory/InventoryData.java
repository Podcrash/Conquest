package me.raindance.champions.inventory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.mc.util.ItemStackUtil;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.SkillInfo;
import me.raindance.champions.kits.enums.InvType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryData {
    public static SkillData itemStackToSkillData(ItemStack itemStack) {
        String name = ChatUtil.purge(itemStack.getItemMeta().getDisplayName());
        for(SkillData data : SkillInfo.getSkillData()) {
            if(name.equalsIgnoreCase(data.getName()))
                return data;
        }
        return null;
    }

    static ItemStack skillToItemStack(SkillData data) {
        Material defaultMat = Material.BOOK;
        if(data.getInvType() == InvType.INNATE) defaultMat = Material.NETHER_STAR;
        ItemStack item = ItemStackUtil.createItem(
                defaultMat,
                ChatColor.BOLD + data.getName(),
                data.getDescription());
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack getInvItem(InvType invType) {
        switch (invType){
            case SWORD:
                return ItemStackUtil.createItem(Material.IRON_SWORD, "Sword Skills", null);
            case AXE:
                return ItemStackUtil.createItem(Material.IRON_AXE, "Axe Skills", null);
            case BOW:
                return ItemStackUtil.createItem(Material.BOW, "Bow Skills", null);
            case SHOVEL:
                return ItemStackUtil.createItem(Material.IRON_SPADE, "Shovel Skills", null);
            case PASSIVEA:
                Dye red = new Dye();
                red.setColor(DyeColor.RED);
                ItemStack itemStack = red.toItemStack(1);
                setItemName(itemStack, "Primary Passive");
                return itemStack;
            case PASSIVEB:
                Dye blue = new Dye();
                blue.setColor(DyeColor.BLUE);
                ItemStack itemStack1 = blue.toItemStack(1);
                setItemName(itemStack1, "Secondary Passive");
                return itemStack1;
            case DROP:
                Dye green = new Dye();
                green.setColor(DyeColor.GREEN);
                ItemStack itemStack2 = green.toItemStack(1);
                setItemName(itemStack2, "Active Ability");
                return itemStack2;
            case INNATE:
                return ItemStackUtil.createItem(Material.DIAMOND, "Innate Passive", null);
            default:
                throw new IllegalArgumentException("Not allowed");
        }
    }
    private static void setItemName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }
}
