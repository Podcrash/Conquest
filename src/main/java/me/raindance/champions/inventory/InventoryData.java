package me.raindance.champions.inventory;

import com.podcrash.api.util.ChatUtil;
import com.podcrash.api.util.ItemStackUtil;
import me.raindance.champions.kits.SkillInfo;
import com.podcrash.api.kits.enums.InvType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                String.format("%s%s%s%s", ChatColor.RESET, ChatColor.AQUA, ChatColor.BOLD, data.getName()),
                data.getDescription());
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
