package me.raindance.champions.inventory;

import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.mc.util.Utility;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChampionsItem {

    private int slotID;
    private String name;
    private int count;
    private int damage;
    private List<String> desc;
    private Material material;
    private byte data = 0;

    public static List<ChampionsItem> details = new ArrayList<>();

    private ChampionsItem(int slotID, String name, int count, int damage, List<String> desc, Material material, byte data) {
        this.slotID = slotID;
        this.name = name;
        this.count = count;
        this.damage = damage;
        this.desc = desc;
        this.material = material;
        this.data = data;

        details.add(this);
    }

    public static final ChampionsItem STANDARD_SWORD = new ChampionsItem(9, ChatColor.WHITE + "Standard Sword", 1, 6, Arrays.asList(ChatColor.GOLD + "Deals 6 damage."), Material.IRON_SWORD);
    public static final ChampionsItem STANDARD_AXE = new ChampionsItem(10, ChatColor.WHITE + "Standard Axe", 1, 6, Arrays.asList(ChatColor.GOLD + "Deals 6 damage."), Material.IRON_AXE);
    public static final ChampionsItem STANDARD_SHOVEL = new ChampionsItem(11, ChatColor.WHITE + "Standard Shovel", 1, 6, Arrays.asList(ChatColor.GOLD + "Deals 6 damage."), Material.IRON_SPADE);

    public static final ChampionsItem BOOSTER_SWORD = new ChampionsItem(12, ChatColor.WHITE + "Spell Sword", 1, 5, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.GOLD_SWORD);
    public static final ChampionsItem BOOSTER_AXE = new ChampionsItem(13, ChatColor.WHITE + "Spell Axe", 1, 5, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.GOLD_AXE);
    public static final ChampionsItem BOOSTER_SHOVEL = new ChampionsItem(14, ChatColor.WHITE + "Spell Shovel", 1, 5, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.GOLD_SPADE);

    public static final ChampionsItem WOOD_SWORD = new ChampionsItem(41, ChatColor.WHITE + "Life Sword", 1, 5, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.WOOD_SWORD);
    public static final ChampionsItem WOOD_AXE = new ChampionsItem(42, ChatColor.WHITE + "Life Axe", 1, 5, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.WOOD_AXE);
    public static final ChampionsItem WOOD_SHOVEL = new ChampionsItem(43, ChatColor.WHITE + "Life Shovel", 1, 5, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.WOOD_SPADE);

    public static final ChampionsItem STONE_SWORD = new ChampionsItem(18, ChatColor.GOLD + "Stone Sword", 1, 6, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.STONE_SWORD);
    public static final ChampionsItem STONE_AXE = new ChampionsItem(19, ChatColor.GOLD + "Stone Axe", 1, 6, Arrays.asList(ChatColor.GOLD + "Deals 5 damage."), Material.STONE_AXE);

    public static final ChampionsItem POWER_SWORD = new ChampionsItem(27, ChatColor.AQUA + "Power Sword", 1, 7, Arrays.asList(ChatColor.GOLD + "A power sword", ChatColor.GOLD + "Deals 7 damage."), Material.DIAMOND_SWORD);
    public static final ChampionsItem POWER_AXE = new ChampionsItem(28, ChatColor.AQUA + "Power Axe", 1, 7, Arrays.asList(ChatColor.GOLD + "A power sword", ChatColor.GOLD + "Deals 7 damage."), Material.DIAMOND_AXE);

    public static final ChampionsItem STANDARD_BOW = new ChampionsItem(29, ChatColor.WHITE + "Standard Bow", 1, Arrays.asList(ChatColor.GOLD + "A regular bow", ChatColor.GOLD + "Use it to shoot people from range!"), Material.BOW);
    public static final ChampionsItem MARKSMAN_ARROWS = new ChampionsItem(20, ChatColor.WHITE + "Ranger Arrows", 40, Arrays.asList(""), Material.ARROW);
    public static final ChampionsItem HUNTER_ARROWS = new ChampionsItem(25, ChatColor.WHITE + "Ranger Arrows", 32, Arrays.asList(""), Material.ARROW);
    public static final ChampionsItem ASSASSIN_ARROWS = new ChampionsItem(21, ChatColor.WHITE + "Assassin Arrows", 16, Arrays.asList(""), Material.ARROW);

    public static final ChampionsItem MUSHROOM_STEW = new ChampionsItem(22, ChatColor.WHITE + "Mushroom Stew", 1, Arrays.asList(ChatColor.GOLD + "When consumed grants Regeneration II for 4 seconds."), Material.MUSHROOM_SOUP);
    public static final ChampionsItem WATER_BOTTLE = new ChampionsItem(31, ChatColor.WHITE + "Water Bottle", 1, Arrays.asList(ChatColor.GOLD + "A Swiggity Swooty", ChatColor.GOLD + "Cure all negative effects!"), Material.POTION);
    public static final ChampionsItem COBWEB = new ChampionsItem(24, ChatColor.WHITE + "Cobweb", 4, Arrays.asList(ChatColor.GOLD + "Left click to throw", ChatColor.GOLD + "a temporary cobweb will be placed upon collision!"), Material.WEB);

    public static final ChampionsItem SMOKE_BOMB = new ChampionsItem(1, ChatColor.WHITE + "Smoke Bomb", 1, 0, Arrays.asList(ChatColor.GOLD + "Right click to toss a Smoke Bomb in target direction,", ChatColor.GOLD + "becoming a puff of smoke after impact and inflicting Blindness and Slowness I", ChatColor.GOLD + "to all players within 4 blocks of the explosion for 3 seconds."), Material.FIREWORK_CHARGE);
    public static final ChampionsItem STUN_CHARGE = new ChampionsItem(2, ChatColor.WHITE + "Stun Charge", 2, 0, Arrays.asList(ChatColor.GOLD + "Right click to drop a Stun Change on the ground.", ChatColor.GOLD + "Enemies that step on the Stun Charge will be Silenced and Shocked for 4 seconds.", ChatColor.GOLD + "Stun Charges disappear after 20 seconds or when you die or change kits."), Material.REDSTONE_LAMP_OFF);
    public static final ChampionsItem MEAD = new ChampionsItem(3, ChatColor.WHITE + "Bread", 1, 0, Arrays.asList(ChatColor.GOLD + "When consumed, grants the user Strength I for 3 seconds. "), Material.BREAD);
    public static final ChampionsItem BEAR_TRAP = new ChampionsItem(4, ChatColor.WHITE + "Bear Trap", 2, 0, Arrays.asList(ChatColor.GOLD + "Right click to drop a Bear Trap.", ChatColor.GOLD + "After dropping a Bear Trap,it will take about 1 second for it to ready itself.", ChatColor.GOLD + "If an enemy steps on it, they will take 3 damage and be Rooted for 2 seconds."), Material.STONE_PLATE);
    public static final ChampionsItem ELIXIR = new ChampionsItem(5, ChatColor.WHITE + "Elixir", 2, 0, Arrays.asList(ChatColor.GOLD + "Right click to drop Elixir, which splashes an aura of healing"), Material.POTION, (byte) 16421);

    private ChampionsItem(int slotID, String name, int count, int damage, List<String> desc, Material material) {
        this(slotID, name, count, damage, desc, material, (byte) 0);
    }
    private ChampionsItem(int slotID, String name, int count, List<String> desc, Material material) {
        this(slotID, name, count, 0, desc, material);
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
    
    public List<String> getDesc() {
        return desc;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack toItemStack(){
        ItemStack itemStack;

        if(this.getName().equals(ChatColor.WHITE + "Elixir")) {
            Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
            potion.setSplash(true);
            itemStack = potion.toItemStack(getCount());
        }
        else {
            itemStack = new ItemStack(material, count, data);
        }
        /*
        switch (this) {
            case ELIXIR:
                Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
                potion.setSplash(true);
                itemStack = potion.toItemStack(1);
                break;
            default:
                itemStack = new ItemStack(material, count, data);
                break;
        }
       */
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
        meta.setDisplayName(ChatColor.RESET + name);
        if(material.equals(Material.GOLD_AXE) || material.equals(Material.GOLD_SWORD) || material.equals(Material.GOLD_SPADE))
            meta.addEnchant(Enchantment.DURABILITY, 5, true);
        List<String> arrays = new ArrayList<>(desc);
        meta.setLore(arrays);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static ChampionsItem getBySlotID(int slotID) {
        for(ChampionsItem item : details) {
            if(item.getSlotID() == slotID) return item;
        }
        return null;
    }

    public static ChampionsItem getByName(String name) {
        name = ChatUtil.purge(name);
        for(ChampionsItem item : details) {
            String unfiltered = ChatUtil.purge(item.getName());
            if(unfiltered.equals(name)) return item;
        }
        return null;
    }
}
