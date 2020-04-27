package me.raindance.champions.inventory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.podcrash.api.db.tables.ChampionsKitTable;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.economy.Currency;
import com.podcrash.api.util.ItemStackUtil;
import com.podcrash.api.util.MathUtil;
import me.raindance.champions.Main;
import com.podcrash.api.kits.KitPlayerManager;
import me.raindance.champions.kits.SkillInfo;
import com.podcrash.api.kits.enums.InvType;
import me.raindance.champions.kits.SkillType;
import me.raindance.champions.util.ConquestUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class MenuCreator {

    public static Inventory createGeneralMenu() {
        ItemStack[] items = ChampionsInventory.getClassItemList();
        int size =  MathUtil.ceil(9, items.length * 2);
        Inventory inventory = Bukkit.createInventory(null, 45, "Class Menu");
        //this is safer, just in case items have some null elements
        /*
        for(ItemStack item : items)
            if(item != null)
                inventory.addItem(item);
        */

        inventory.setItem(9, items[0]);
        inventory.setItem(11, items[2]);
        inventory.setItem(13, items[4]);
        inventory.setItem(15, items[6]);
        inventory.setItem(17, items[8]);
        inventory.setItem(27, items[1]);
        inventory.setItem(29, items[3]);
        inventory.setItem(31, items[5]);
        inventory.setItem(33, items[7]);
        inventory.setItem(35, items[9]);

        return inventory;
    }

    private static int calculateRows(SkillType skillType) {
        Set<InvType> invTypes = new HashSet<>();
        SkillInfo.skillsConsumer(skillType, skill -> invTypes.add(skill.getInvType()));
        return invTypes.size();
    }

     public static Inventory createKitMenu(SkillType skillType) {
     return createKitMenu(skillType, new Integer[0]);
     }
     /**
     * Create the kit menu
     * @param skillType
     * @param skillIDs
     * @return
     */
    public static Inventory createKitMenu(SkillType skillType, Integer[] skillIDs) {
        Set<Integer> skillSet = new HashSet<>(Arrays.asList(skillIDs));
        final int rows = calculateRows(skillType);
        System.out.println("Creating inventory with " + rows + " rows!");
        String title = ChatColor.DARK_GRAY + skillType.getName();
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        Main.getInstance().getLogger().info("Inventory max size: " + inventory.getSize());
        int cursor = 0;
        //get all the invtypes
        invOuter:
        for(InvType invType : InvType.details()) {
            //get all the skills related to the invtype
            List<SkillData> dataList = SkillInfo.getSkills(invType);
            boolean skillPresent = false;
            //make a cursor for the inventory
            int entry = cursor + 1;
            for(SkillData data : dataList) {
                //if the skilltype doesn't correspond, skip
                if(data.getSkillType() != skillType) continue;
                skillPresent = true;
                ItemStack item = InventoryData.skillToItemStack(data);
                if(skillSet.contains(data.getId()) || data.getInvType() == InvType.INNATE)
                    item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                inventory.setItem(entry, item);
                entry++;
                //set the item and move the cursor
            }
            if(skillPresent) {
                //if the yielded list has skills for it, then set the item tag and move the primary
                //cursor over 1 row
                inventory.setItem(cursor, invType.createItemStack());
                cursor += 9;
            }
        }
        return inventory;
    }

    public static void openTeamSelectMenu(Player player) {
        Inventory inv = player.getInventory();
        ItemStackUtil.createItem(inv, 35, 14,1, 21, "&c&lRed Team");
        ItemStackUtil.createItem(inv, 35, 11,1, 25, "&9&lBlue Team");
    }

    public static Inventory createConfirmationMenu(String item, double cost) {
        SkillData data = SkillInfo.getSkillFromStrippedName(item);
        String title = String.format("%sPurchasing: %s",ChatColor.DARK_GRAY, data.getName());
        Inventory inv = Bukkit.createInventory(null, 3 * 9, title);
        ItemStack price = ItemStackUtil.createItem(Material.EMPTY_MAP, String.format("%S%sPrice: %s%d",
                ChatColor.DARK_GRAY,
                ChatColor.BOLD,
                Currency.GOLD.getFormatting(),
                (int)data.getPrice()), null);
        ItemStack confirmation = ItemStackUtil.createItem(Material.EMERALD_BLOCK, String.format("%s%sConfirm", ChatColor.GREEN, ChatColor.BOLD), null);
        ItemStack cancellation = ItemStackUtil.createItem(Material.REDSTONE_BLOCK, String.format("%s%sDeny", ChatColor.RED, ChatColor.BOLD), null);
        ItemStack info = InventoryData.skillToItemStack(data);
        inv.setItem(4, price);
        inv.setItem(11, confirmation);
        inv.setItem(13, info);
        inv.setItem(15,cancellation);
        return inv;
    }

    public static void giveHotbarInventory(Player player, SkillType skillType) {
        Inventory inventory = player.getInventory();
        ChampionsInventory.setHotBar(inventory, skillType);
        player.updateInventory();
    }
    public static void giveHotBarInventory(Player player, JsonObject itemsJson) {
        Inventory inventory = player.getInventory();
        for(Map.Entry<String, JsonElement> entry : itemsJson.entrySet()) {
            String slotKey = entry.getKey();
            int itemID = entry.getValue().getAsInt();
            if(itemID == -1) continue;
            ChampionsItem championsItem = ChampionsItem.getBySlotID(itemID);
            inventory.setItem(Integer.parseInt(slotKey), championsItem.toItemStack());
        }

        player.updateInventory();
    }

    private static Inventory createCopyMenu(String color, String stype) {
        return Bukkit.createInventory(null, 54, String.format("%s%s", color, stype));
    }
    private static Inventory createCopyMenu(String stype) {
        return createCopyMenu(ChatColor.DARK_GRAY.toString(), stype);
    }

    public static Inventory createKitTemplate(Player player, SkillType skillType) {
        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        Inventory inventory = createCopyMenu(ChatColor.DARK_GRAY.toString(), skillType.getName() + " Build");
        DyeColor[] colors = new DyeColor[]{DyeColor.RED, DyeColor.BLUE, DyeColor.YELLOW, DyeColor.GREEN};
        int[] rowStarts = new int[] {20, 22, 24, 26};

        Material[] materials = new Material[] {Material.INK_SACK, Material.ANVIL, Material.SLIME_BALL, Material.FIREBALL};
        String[] names = new String[] {String.format("%s%s", ChatColor.RESET, "Apply Build "),
                String.format("%s%s", ChatColor.RESET, "Edit Build "),
                String.format("%s%s", ChatColor.RESET, "Set as Default: Build "),
                String.format("%s%s%s", ChatColor.RESET, ChatColor.RED, "Delete Build ")};
        int[] slots = new int[] {0, 9, 18, 27};

        UUID uuid = player.getUniqueId();
        String clasz = skillType.getName();

        for(int i = 1; i < 5; i++) {
            final int index = i - 1;
            DyeColor color = colors[index];
            for(int k = 0; k < materials.length; k++) {
                Material material = materials[k];
                String name = names[k] + i;
                final int slot = rowStarts[index] + slots[k];
                final ItemStack item = new ItemStack(material);
                final ItemMeta meta = item.getItemMeta();


                meta.setDisplayName(name);
                item.setItemMeta(meta);
                inventory.setItem(slot, item);

                if(name.contains("Apply Build")) {
                    table.getJSONDataAsync(uuid, clasz, i).thenAccept(dataJSON -> {
                        ItemStack duplicate =  new ItemStack(Material.INK_SACK, 1, color.getData());
                        ItemMeta futureMeta = item.getItemMeta();

                        futureMeta.setLore(ConquestUtil.readSkills(dataJSON));
                        duplicate.setItemMeta(futureMeta);
                        inventory.setItem(slot, duplicate);
                    });
                }
            }
        }

        //==========
        //Creating the default kit applier
        ItemStack defItemStack = new ItemStack(Material.EMERALD);
        ItemMeta defMeta = defItemStack.getItemMeta();
        defMeta.setDisplayName(ChatColor.RESET + "Apply Default Build");


        String data = table.getJSONData(uuid, clasz, 0);
        if(data == null) {
            data = ConquestUtil.getDefaultSerialized(skillType);
            table.set(uuid, clasz, 0, data);
        }
        //System.out.println(data);
        defMeta.setLore(ConquestUtil.readSkills(data));
        defItemStack.setItemMeta(defMeta);
        inventory.setItem(18,defItemStack);

        return inventory;
    }
}
