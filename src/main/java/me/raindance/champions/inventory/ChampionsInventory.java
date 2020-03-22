package me.raindance.champions.inventory;

import com.podcrash.api.mc.util.ItemStackUtil;
import me.raindance.champions.Main;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChampionsInventory {
    private static final ItemStack[] classItemList;
    private static int cursor = 0;
    static {
        classItemList = new ItemStack[SkillType.details().length];

        addClass(SkillType.Warden, Material.IRON_HELMET,
                "Wardens function as off-tanks, boasting",
                "damage-soaking capabilities and crowd",
                "control. While they lack mobility, they",
                "make up for it with their considerable",
                "skirmishing ability.");

        addClass(SkillType.Duelist, Material.DIAMOND_SWORD,
                "Duelists excel in fighting enemies one",
                "by one and dealing consistent single-target",
                "damage. They primarily rely on their melee",
                "attacks to cut their opponents down.");

        addClass(SkillType.Vanguard, Material.DIAMOND_CHESTPLATE,
                "Vanguards are resilient and powerful",
                        "teamfighters. They sacrifice damage in",
                        "exchange for strong crowd control",
                        "abilities and high durability.");
        
        addClass(SkillType.Berserker, Material.DIAMOND_AXE, 
                "Berserkers are mobile, close-range",
                        "fighters capable of dealing with multiple",
                        "enemies. Though somewhat frail, they can",
                        "become a huge threat in teamfights if left",
                        "unchecked.");

        addClass(SkillType.Marksman, Material.BOW, 
                "Marksmen rely on their precise, long-ranged",
                        "attacks to support their team. They exclusively",
                        "use their bow to deal heavy damage and",
                        "cripple their targets.");

        addClass(SkillType.Hunter, Material.BONE,
                "Hunters are nimble archers that shine in",
                        "both short-range and mid-range engagements.",
                        "They are able to use their bow to keep strong",
                        "enemies at a distance and to secure kills on",
                        "the weak.");

        addClass(SkillType.Sorcerer, Material.BLAZE_ROD,
                "Sorcerers depend on their abilities to",
                        "dish out damage from a range. They are",
                        "exceptional at whittling down their opponents",
                        "over time and setting up potential kills with",
                        "their spells.");

        addClass(SkillType.Druid, Material.SAPLING,
                "Druids act as supports for their team, empowering",
                        "their allies with enchantments and protecting",
                        "them with healing magic. In addition, they offer",
                        "a substantial amount of utility in team-fights with",
                        "their crowd control.");

        addClass(SkillType.Rogue, Material.LEATHER_BOOTS,
                "Rogues are agile assassins who specialize in",
                        "taking down fragile priority targets. They depend",
                        "on their mobility to close the gap between their",
                        "enemies and their high burst damage to dispatch",
                        "them quickly.");

        addClass(SkillType.Thief, Material.COAL, 
                "Thieves are cunning and elusive, adept at",
                        "disorienting their adversaries with a plethora of",
                        "tools and abilities. Rather than eliminating the",
                        "opposition themselves, they aim to distract them",
                        "for as long as possible while their allies capitalize",
                        "off the confusion.");
        /*
        ItemStack assassin = ItemStackUtil.createItem(Material.LEATHER_HELMET, "Assassin", Arrays.asList("Use stealth hacks", "and insane mobility to kill every1"));
        ItemStack brute = ItemStackUtil.createItem(Material.DIAMOND_HELMET, "Brute", Arrays.asList("Use crowd control", "and filth to kill every1"));
        ItemStack mage = ItemStackUtil.createItem(Material.GOLD_HELMET, "Mage", Arrays.asList("Use insane IQ", "and insane skills to kill every1"));
        ItemStack knight = ItemStackUtil.createItem(Material.IRON_HELMET, "Knight", Arrays.asList("Use defense", "and brain to kill every1"));
        ItemStack ranger = ItemStackUtil.createItem(Material.CHAINMAIL_HELMET, "Ranger", Arrays.asList("Use mobility", "and range to kill every1"));
        */
        cursor = 0;
    }

    private static void addClass(SkillType skillType, Material material, String... description) {
        List<String> desc = new ArrayList<>();
        for(String d : description) {
            desc.add(ChatColor.GRAY + d);
        }
        ItemStack item = ItemStackUtil.createItem(material, String.format("%s%s%s", ChatColor.RESET, ChatColor.BOLD, skillType.getName()), desc);
        classItemList[cursor] = item;
        cursor++;
    }

    /**
     * Clear the player's inventory
     * @param player
     */
    public static void clearHotbarSelection(Player player) {
        Inventory inv = player.getInventory();
        for(int i = 9; i < 36; i++) {
            inv.setItem(i, new ItemStack(Material.AIR, 1));
        }
        Bukkit.getScheduler().runTaskLater(Main.instance, player::updateInventory, 1L);
    }

    public static ItemStack[] getClassItemList() {
        return classItemList;
    }

    static void setHotBar(Inventory inventory, SkillType skillType) {
        ChampionsItem[] itemArray;
        switch (skillType) {
            //they won't be merged just in case for easy access
            case Warden:
                itemArray = new ChampionsItem[]{ChampionsItem.WARDEN_SWORD, ChampionsItem.WARDEN_AXE, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            case Vanguard:
                itemArray = new ChampionsItem[] {ChampionsItem.VANGUARD_SHOVEL, ChampionsItem.VANGUARD_AXE, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            case Berserker:
                itemArray = new ChampionsItem[] {ChampionsItem.BERSERKER_AXE, ChampionsItem.BREAD, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            case Duelist:
                itemArray = new ChampionsItem[] {ChampionsItem.DUELIST_SWORD, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            case Marksman:
                itemArray = new ChampionsItem[] {ChampionsItem.MARKSMAN_SWORD, ChampionsItem.MARKSMAN_BOW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MARKSMAN_ARROWS};
                break;
            case Hunter:
                itemArray = new ChampionsItem[] {ChampionsItem.HUNTER_SWORD, ChampionsItem.HUNTER_AXE, ChampionsItem.HUNTER_BOW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.BEAR_TRAP, ChampionsItem.HUNTER_ARROWS};
                break;
            case Thief:
                itemArray = new ChampionsItem[] {ChampionsItem.THIEF_SWORD, ChampionsItem.THIEF_AXE, ChampionsItem.THIEF_BOW, ChampionsItem.THIEF_ARROWS, ChampionsItem.STUN_CHARGE, ChampionsItem.COBWEB, ChampionsItem.SMOKE_BOMB, ChampionsItem.ELIXIR};
                break;
            case Rogue:
                itemArray = new ChampionsItem[] {ChampionsItem.ROGUE_SWORD, ChampionsItem.ROGUE_AXE, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            case Druid:
                itemArray = new ChampionsItem[] {ChampionsItem.LIFE_SWORD, ChampionsItem.LIFE_SHOVEL, ChampionsItem.LIFE_AXE, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            case Sorcerer:
                itemArray = new ChampionsItem[] {ChampionsItem.SPELL_SWORD, ChampionsItem.SPELL_SHOVEL, ChampionsItem.SPELL_AXE, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW, ChampionsItem.MUSHROOM_STEW};
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + skillType);
        }

        for(int i = 0; i < itemArray.length; i++) {
            ChampionsItem id = itemArray[i];
            inventory.setItem(i, id.toItemStack());
        }
    }
}
