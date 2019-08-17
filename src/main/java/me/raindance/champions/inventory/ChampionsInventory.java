package me.raindance.champions.inventory;

import me.raindance.champions.Main;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ChampionsInventory {
    private static final ItemStack[] classItemList;

    static {
        ItemStack assassin = createItem(Material.LEATHER_HELMET, "Assassin", Arrays.asList("Use stealth hacks", "and insane mobility to kill every1"));
        ItemStack brute = createItem(Material.DIAMOND_HELMET, "Brute", Arrays.asList("Use crowd control", "and filth to kill every1"));
        ItemStack mage = createItem(Material.GOLD_HELMET, "Mage", Arrays.asList("Use insane IQ", "and insane skills to kill every1"));
        ItemStack knight = createItem(Material.IRON_HELMET, "Knight", Arrays.asList("Use defense", "and brain to kill every1"));
        ItemStack ranger = createItem(Material.CHAINMAIL_HELMET, "Ranger", Arrays.asList("Use mobility", "and range to kill every1"));
        classItemList = new ItemStack[]{assassin, brute, mage, knight, ranger};
    }

    public static Inventory getClassSelection() {
        Inventory menu = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Class Selection");
        menu.addItem(classItemList);
        return menu;
    }

    /**
     * Set the player's inventory to a hotbar selection
     * @param player
     * @param skillType
     */
    public static void getHotbarSelection(Player player, SkillType skillType){
        Inventory inventory = player.getInventory();
        inventory.clear();
        for(ChampionsItem item : ChampionsItem.values()) {
            SkillType[] skillTypes = item.getSkillType();
            if(contains(skillTypes, skillType) || contains(skillTypes, SkillType.Global)) {
                ItemStack itemStack = item.toItemStack();
                inventory.setItem(item.getSlotID(), itemStack);
            }
        }
        int tokenAmount = 10;
        if(skillType == SkillType.Assassin || skillType == SkillType.Ranger) tokenAmount = 12;

        inventory.setItem(17, new ItemStack(Material.IRON_INGOT, tokenAmount));
    }
    private static boolean contains(SkillType[] skillTypes, SkillType skillType) {
        for(int i = 0; i < skillTypes.length; i++){
            if(skillTypes[i] == skillType) return true;
        }
        return false;
    }

    /**
     * Clear the player's inventory
     * @param player
     */
    public static void clearHotbarSelection(Player player) {
        Inventory inv = player.getInventory();
        for(ChampionsItem item : ChampionsItem.values()) {
            if(inv.getItem(item.getSlotID()) != null) {
                inv.setItem(item.getSlotID(), new ItemStack(Material.AIR, 1));
            }
        }
        inv.setItem(17, new ItemStack(Material.AIR));
        Bukkit.getScheduler().runTaskLater(Main.instance, player::updateInventory, 1L);
    }

    private static ItemStack createItem(Material material, String name, List<String> desc) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(desc);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack[] getClassItemList() {
        return classItemList;
    }
}
