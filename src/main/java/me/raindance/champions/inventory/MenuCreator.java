package me.raindance.champions.inventory;

import com.google.common.collect.BiMap;
import com.podcrash.api.db.ChampionsKitTable;
import com.podcrash.api.db.DataTableType;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.mc.game.GameManager;
import me.raindance.champions.inventory.update.IUpdateInv;
import me.raindance.champions.inventory.update.InventoryUpdater;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuCreator {
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static Inventory generalMenu;
    private static Inventory bruteInv;
    private static Inventory knightInv;
    private static Inventory rangerInv;
    private static Inventory assassinInv;
    private static Inventory mageInv;

    /**
     * Responsible for creating a game menu that constantly updates (kinda)
     */
    private static IUpdateInv gamesMenu = new IUpdateInv() {
        Inventory gameMenu = createCopyMenu(ChatColor.BOLD.toString() + ChatColor.DARK_AQUA, "Current Champions Games");
        @Override
        public Inventory getInventory() {
            return gameMenu;
        }

        @Override
        public void update() {
            getInventory().clear();
            getInventory().addItem(GameManager.getGame().getItemInfo());
        }
    };

    static {
        InventoryUpdater.add(gamesMenu);
    }
    private final static HashMap<InvType, Integer> invIntMap = new HashMap<>();

    static {
        final int sword = 0;
        final int axe = sword + 9;
        final int bow = axe + 9;
        final int passA = bow + 9;
        final int passB = passA + 9;
        final int passC = passB + 9;
        invIntMap.put(InvType.SWORD, sword);
        invIntMap.put(InvType.AXE, axe);
        invIntMap.put(InvType.BOW, bow);
        invIntMap.put(InvType.PASSIVEA, passA);
        invIntMap.put(InvType.PASSIVEB, passB);
        invIntMap.put(InvType.PASSIVEC, passC);
    }

    public static Inventory createGeneralMenu() {
        if (generalMenu != null) return generalMenu;
        Inventory inventory = Bukkit.createInventory(null, 9, "Class Menu");
        inventory.addItem(ChampionsInventory.getClassItemList());
        generalMenu = inventory;
        return inventory;
    }
    public static Inventory createMenu(BiMap<Integer, Skill> idSkillMap, Set<Integer> classSet, SkillType stype) {
        Inventory decider = basicSetter(Bukkit.createInventory(null, 54, String.format("%s%s", ChatColor.GREEN, stype.toString())));
        idSkillMap.forEach((key, skill) -> {
            if (classSet.contains(key)) {
                BookFormatter book = InventoryData.getSkillFormatter(skill);
                ItemStack itemBook = new ItemStack(Material.BOOK);
                ItemMeta meta = itemBook.getItemMeta();
                meta.setDisplayName(book.getHeader(0));
                meta.setLore(book.getDescription(0));
                itemBook.setItemMeta(meta);
                for (int b = 0; b <= 8; b++) {
                    int a = invIntMap.get(skill.getInvType()) + b;
                    if (decider.getItem(a) == null) {
                        decider.setItem(a, itemBook);
                        break;
                    }
                }

            }
        });
        switch (stype) {
            case Brute:
                bruteInv = decider;
                break;
            case Mage:
                mageInv = decider;
                break;
            case Ranger:
                rangerInv = decider;
                break;
            case Assassin:
                assassinInv = decider;
                break;
            case Knight:
                knightInv = decider;
                break;
        }
        ItemStack goldTokens = new ItemStack(Material.GOLD_INGOT);
        goldTokens.setAmount(12);
        decider.setItem(8, goldTokens);
        return decider;
    }
    private static Inventory basicSetter(Inventory inventory) {
        Dye red = new Dye();
        red.setColor(DyeColor.RED);
        Dye blue = new Dye();
        blue.setColor(DyeColor.BLUE);
        Dye green = new Dye();
        green.setColor(DyeColor.GREEN);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        setItemName(sword, "Sword Skills");
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        setItemName(axe, "Axe Skills");
        ItemStack bow = new ItemStack(Material.BOW);
        setItemName(bow, "Bow Skills");

        ItemStack redi = new ItemStack(red.toItemStack(1));
        setItemName(redi, "Passive A");
        ItemStack bluei = new ItemStack(blue.toItemStack(1));
        setItemName(bluei, "Passive B");
        ItemStack greeni = new ItemStack(green.toItemStack(1));
        setItemName(greeni, "Passive C");

        inventory.setItem(invIntMap.get(InvType.SWORD), sword);
        inventory.setItem(invIntMap.get(InvType.AXE), axe);
        inventory.setItem(invIntMap.get(InvType.BOW), bow);
        inventory.setItem(invIntMap.get(InvType.PASSIVEA), redi);
        inventory.setItem(invIntMap.get(InvType.PASSIVEB), bluei);
        inventory.setItem(invIntMap.get(InvType.PASSIVEC), greeni);
        return inventory;
    }

    public static Inventory getBruteInv() {
        if (bruteInv == null) {
            bruteInv = MenuCreator.createMenu(InventoryData.getIdSkillMap(), InventoryData.getBruteSet(), SkillType.Brute);
        }
        Inventory inv = createCopyMenu("Brute");
        inv.setContents(bruteInv.getContents());
        return inv;
    }
    public static Inventory getKnightInv() {
        if (knightInv == null) {
            knightInv = MenuCreator.createMenu(InventoryData.getIdSkillMap(), InventoryData.getKnightSet(), SkillType.Knight);
        }
        Inventory inv = createCopyMenu("Knight");
        inv.setContents(knightInv.getContents());
        return inv;
    }
    public static Inventory getRangerInv() {
        if (rangerInv == null) {
            rangerInv = MenuCreator.createMenu(InventoryData.getIdSkillMap(), InventoryData.getRangerSet(), SkillType.Ranger);
        }
        Inventory inv = createCopyMenu("Ranger");
        inv.setContents(rangerInv.getContents());
        return inv;
    }
    public static Inventory getAssassinInv() {
        if (assassinInv == null) {
            assassinInv = MenuCreator.createMenu(InventoryData.getIdSkillMap(), InventoryData.getAssassinSet(), SkillType.Assassin);
        }
        Inventory inv = createCopyMenu("Assassin");
        inv.setContents(assassinInv.getContents());
        return inv;
    }
    public static Inventory getMageInv() {
        if (mageInv == null) {
            mageInv = MenuCreator.createMenu(InventoryData.getIdSkillMap(), InventoryData.getMageSet(), SkillType.Mage);
        }
        Inventory inv = createCopyMenu("Mage");
        inv.setContents(mageInv.getContents());
        return inv; //test
    }

    public static Inventory getInv(SkillType skillType) {
        switch (skillType) {
            case Knight:
                return getKnightInv();
            case Assassin:
                return getAssassinInv();
            case Ranger:
                return getRangerInv();
            case Mage:
                return getMageInv();
            case Brute:
                return getBruteInv();
        }
        return createCopyMenu("something went wrong? report to raindanxe MC");
    }
    private static void setItemName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }

    private static Inventory createCopyMenu(String color, String stype) {
        return Bukkit.createInventory(null, 54, String.format("%s%s", color, stype));
    }
    private static Inventory createCopyMenu(String stype) {
        return createCopyMenu(ChatColor.GREEN.toString(), stype);
    }

    public static Inventory createGameMenu() {
        return gamesMenu.getInventory();
    }

    public static Inventory createKitTemplate(Player player, SkillType skillType) {
        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        Inventory inventory = createCopyMenu(ChatColor.BLACK.toString(), skillType.getName() + " Build");
        DyeColor[] colors = new DyeColor[]{DyeColor.RED, DyeColor.BLUE, DyeColor.YELLOW, DyeColor.GREEN};
        int[] rowStarts = new int[] {20, 22, 24, 26};

        Material[] materials = new Material[] {Material.INK_SACK, Material.ANVIL, Material.FIREBALL};
        String[] names = new String[] {"Apply Build ", "Edit Build ", "Delete Build "};
        int[] slots = new int[] {0, 9, 27};

        UUID uuid = player.getUniqueId();
        String clasz = skillType.getName();
        for(int i = 1; i < 5; i++) {
            final int index = i - 1;
            DyeColor color = colors[index];
            for(int k = 0; k < materials.length; k++) {
                Material material = materials[k];
                String name = names[k] + i;
                int slot = rowStarts[index] + slots[k];
                ItemStack item = (material == Material.INK_SACK) ?
                        new ItemStack(material, 1, DyeColor.GRAY.getData()) :
                        new ItemStack(material);
                final ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(name);
                String dataJSON = table.getJSONData(uuid, clasz, i);
                if(name.contains("Apply Build") && dataJSON != null) {
                    Dye data = ((Dye) item.getData());
                    data.setColor(color);
                    item = new ItemStack(Material.INK_SACK, 1, data.getData());
                    meta.setLore(ChampionsPlayerManager.getInstance().readSkills(dataJSON));
                }
                item.setItemMeta(meta);
                inventory.setItem(slot, item);
            }
        }
        return inventory;
    }
}
