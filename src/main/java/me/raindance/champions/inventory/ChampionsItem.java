package me.raindance.champions.inventory;

import me.raindance.champions.Main;
import me.raindance.champions.kits.enums.SkillType;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ChampionsItem {
    STANDARD_SWORD(9, ChatColor.WHITE + "Standard Sword", 1, 2, 6, Arrays.asList(ChatColor.GOLD + "A regular iron sword"), Material.IRON_SWORD, SkillType.Global),
    STANDARD_AXE(10, ChatColor.WHITE + "Standard Axe", 1, 2, 6, Arrays.asList(ChatColor.GOLD + "A regular iron axe"), Material.IRON_AXE, SkillType.Global),
    BOOSTER_SWORD(18, ChatColor.GOLD + "Booster Sword", 1, 3, 6, Arrays.asList(ChatColor.GOLD + "A boosted standard sword", ChatColor.GOLD + "that increases the level of the", ChatColor.GOLD + "skill binded by 2!", ChatColor.GRAY + "Please note that you have to", ChatColor.GRAY + "re-apply this kit to use booster weapons.", ChatColor.GRAY +"Sorry for the inconvenience."), Material.GOLD_SWORD, SkillType.Global),
    BOOSTER_AXE(19, ChatColor.GOLD + "Booster Axe", 1, 3, 6, Arrays.asList(ChatColor.GOLD + "A boosted standard axe", ChatColor.GOLD + "that increases the level of the", ChatColor.GOLD + "skill binded by 2!", ChatColor.GRAY + "Please note that you have to", ChatColor.GRAY + "re-apply this kit to use booster weapons.", ChatColor.GRAY +"Sorry for the inconvenience."), Material.GOLD_AXE, SkillType.Global),
    POWER_SWORD(27, ChatColor.AQUA + "Power Sword", 1, 4, 7, Arrays.asList(ChatColor.GOLD + "A power sword", ChatColor.GOLD + "does more damage", ChatColor.GOLD + "than standard swords!"), Material.DIAMOND_SWORD, SkillType.Global),
    POWER_AXE(28, ChatColor.AQUA + "Power Axe", 1, 4,  7, Arrays.asList(ChatColor.GOLD + "A power sword", ChatColor.GOLD + "does more damage", ChatColor.GOLD + "than standard axes!"), Material.DIAMOND_AXE, SkillType.Global),
    STANDARD_BOW(29, ChatColor.WHITE + "Standard Bow", 1, 1, Arrays.asList(ChatColor.GOLD + "A regular bow", ChatColor.GOLD + "Use it to shoot people from range!"), Material.BOW, SkillType.Ranger, SkillType.Assassin),
    RANGER_ARROWS(20, ChatColor.WHITE + "Ranger Arrows", 24, 1, Arrays.asList(""), Material.ARROW, SkillType.Ranger),
    ASSASSIN_ARROWS(20, ChatColor.WHITE + "Assassin Arrows", 12, 1, Arrays.asList(""), Material.ARROW, SkillType.Assassin),
    MUSHROOM_STEW(22, ChatColor.WHITE + "Mushroom Stew", 1, 1, Arrays.asList(ChatColor.GOLD + "Restore your health!", ChatColor.GOLD + "Gives regeneration II for 4 seconds!"), Material.MUSHROOM_SOUP, SkillType.Global),
    WATER_BOTTLE(31, ChatColor.WHITE + "Water Bottle", 1, 1, Arrays.asList(ChatColor.GOLD + "A Swiggity Swooty", ChatColor.GOLD + "Cure all negative effects!"), Material.POTION, SkillType.Global),
    COBWEB(24, ChatColor.WHITE + "Cobweb", 4, 1, Arrays.asList(ChatColor.GOLD + "Left click to throw", ChatColor.GOLD + "a temporary cobweb will be placed upon collision!"), Material.WEB, SkillType.Global),
    ;

    private int slotID;
    private String name;
    private int count;
    private int tokenCost;
    private int damage;
    private SkillType[] sKillType;
    private List<String> desc;
    private Material material;

    ChampionsItem(int slotID, String name, int count, int tokenCost, int damage, List<String> desc, Material material, SkillType... sKillTypes) {
        this.slotID = slotID;
        this.name = name;
        this.count = count;
        this.tokenCost = tokenCost;
        this.damage = damage;
        this.desc = desc;
        this.material = material;
        this.sKillType = sKillTypes;
    }

    ChampionsItem(int slotID, String name, int count, int tokenCost, List<String> desc, Material material, SkillType... sKillTypes) {
        this(slotID, name, count, tokenCost, 0, desc, material, sKillTypes);
    }

    public int getSlotID() {
        return slotID;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getTokenCost() {
        return tokenCost;
    }

    public SkillType[] getSkillType() {
        return sKillType;
    }

    public List<String> getDesc() {
        return desc;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack toItemStack(){
        ItemStack itemStack = new ItemStack(material, count, (byte) 0);
        if(Enchantment.DURABILITY.canEnchantItem(itemStack)) {
            /*Not sure which one is correct

            NbtCompound unbreakableTag = (NbtCompound) NbtFactory.fromItemTag(CraftItemStack.asCraftCopy(itemStack));
            // according to this right? https://minecraft.gamepedia.com/Player.dat_format#Item_structure
            unbreakableTag.put("Unbreakable", 1);
            */
            //will just use this one for now
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);

            NBTTagList modifiers = new NBTTagList();
            NBTTagCompound damager = new NBTTagCompound();
            damager.set("AttributeName", new NBTTagString("generic.attackDamage"));
            damager.set("Name", new NBTTagString("generic.attackDamage"));
            damager.set("Amount", new NBTTagInt(damage));
            damager.set("Operation", new NBTTagInt(0));
            damager.set("UUIDLeast", new NBTTagInt(894654));
            damager.set("UUIDMost", new NBTTagInt(2872));

            modifiers.add(damager);
            tag.set("AttributeModifiers", modifiers);
            nmsStack.setTag(tag);
            itemStack = CraftItemStack.asBukkitCopy(nmsStack);

        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        if(material.equals(Material.GOLD_AXE) || material.equals(Material.GOLD_SWORD)) meta.addEnchant(Enchantment.DURABILITY, 5, true);
        List<String> arrays = new ArrayList<>(desc);
        arrays.addAll(Arrays.asList("", ChatColor.DARK_PURPLE + "Item Token Cost: " + ChatColor.BOLD + ChatColor.GOLD + Integer.toString(tokenCost)));
        meta.setLore(arrays);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static ChampionsItem getBySlotID(int slotID) {
        for(ChampionsItem item : ChampionsItem.values()) {
            if(item.getSlotID() == slotID) return item;
        }
        return null;
    }
    public static ChampionsItem getBy(int slotID, SkillType type) {
        for(ChampionsItem item : ChampionsItem.values()) {
            if(item.getSlotID() == slotID) {
                Main.getInstance().getLogger().info(item.getSlotID() + " " + item.getName());
                for(SkillType type1 : item.getSkillType()) {
                    if(type1 == type || type1 == SkillType.Global) return item;
                }
            }
        }
        return null;
    }

    public static ChampionsItem getByName(String name) {
        for(ChampionsItem item : ChampionsItem.values()) {
            if(item.getName().equals(name)) return item;
        }
        return null;
    }
}
