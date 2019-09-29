package me.raindance.champions.inventory;


import com.google.gson.JsonObject;
import me.raindance.champions.Configurator;
import me.raindance.champions.Main;
import me.raindance.champions.db.ChampionsKitTable;
import me.raindance.champions.db.DataTableType;
import me.raindance.champions.db.TableOrganizer;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.classes.*;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.sound.SoundPlayer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class InvFactory {
    private static ChampionsKitTable table;
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static Map<String, Integer> buildMap = new HashMap<>();
    private static final Configurator kitConfigurator = Main.getConfigurator("kits");
    private InvFactory() {

    }

    private static ChampionsKitTable getKitTable() {
        if(table == null) table = TableOrganizer.getTable(DataTableType.KITS);
        return table;
    }
    /**
     * Take a inventory menu and turn it into a list of skills.
     * It will iterate through the inventory looking for enchanted books.
     * Then it will find the corresponding bookformatter, which will give its constructor.
     * @param player the user
     * @param menu the menu
     * @return the list of skills
     */
    private static List<Skill> convertInventoryToSkills(Player player, Inventory menu) {
        List<Skill> skills = new ArrayList<>();
        for (ItemStack book : menu.getContents()) {
            //only pass if it has the enchantent
            if(book == null || !book.containsEnchantment(Enchantment.DAMAGE_ALL)) continue;
            Skill skill = InventoryData.getSkill(book);
            if(skill == null) continue;
            BookFormatter formatter = InventoryData.getSkillFormatter(skill);
            Skill _skill = formatter.newInstance(player, book.getAmount());
            skills.add(_skill);
        }
        return skills;
    }

    /**
     * Usually used after {@link this#convertInventoryToSkills(Player, Inventory)}
     * This will clear any duplicates by getting rid of one of the
     * skills whose invtypes are similar.
     * Ex: You cannot have evade or illusion at the same time.
     * @param skills
     */
    private static void removeDuplicatedInvTypes(List<Skill> skills) {
        //really bad check for skills of a different type
        List<InvType> invTypes = new ArrayList<>();
        Iterator<Skill> skillIterator = skills.iterator();
        while(skillIterator.hasNext()) {
            Skill skill = skillIterator.next();
            InvType type = skill.getInvType();
            // it just won't register :{
            if(invTypes.contains(type)) skillIterator.remove();
            else invTypes.add(type);
        }
    }

    /**
     * Shorthand for a constructor of a ChampionsPlayer
     * @param skillType
     * @param player
     * @param skills
     * @return
     */
    private static ChampionsPlayer findViaSkillType(SkillType skillType, Player player, List<Skill> skills) {
        ChampionsPlayer newPlayer;
        switch (skillType){
            case Brute:
                newPlayer = new Brute(player, skills);
                break;
            case Mage:
                newPlayer = new Mage(player, skills);
                break;
            case Ranger:
                newPlayer = new Ranger(player, skills);
                break;
            case Assassin:
                newPlayer = new Assassin(player, skills);
                break;
            case Knight:
                newPlayer = new Knight(player, skills);
                break;
            default:
                throw new IllegalArgumentException("Skilltype cannot be all. from InvFactory:57");
        }
        return newPlayer;
    }

    /**
     * Turn an inventory using the above methods {@link #convertInventoryToSkills(Player, Inventory)} {@link #removeDuplicatedInvTypes(List)} {@link #findViaSkillType(SkillType, Player, List)}
     * This will turn the inventory into a list of skills.
     * Then, remove the duplicated types.
     * Then, register the player.
     * @param player
     * @param menu
     * @param skillType
     * @return
     */
    public static ChampionsPlayer inventoryToChampion(Player player, Inventory menu, SkillType skillType) {
        List<Skill> skills = convertInventoryToSkills(player, menu);
        removeDuplicatedInvTypes(skills);
        ChampionsPlayer newPlayer = findViaSkillType(skillType, player, skills);
        newPlayer.setDefaultHotbar();

        return newPlayer;
    }

    /**
     * Handles either
     * Applying - {@link #apply(Player, SkillType, int)}
     * Editing - {@link  #edit(Player, SkillType, int)}
     * Or
     * Deleting - {@link #delete(Player, SkillType, int)}
     * @param player
     * @param skillType
     * @param item
     */
    public static void clickAtBuildMenu(Player player, SkillType skillType, ItemStack item) {
        if(item == null || item.getType() == Material.AIR) return;
        int buildID = Integer.valueOf(item.getItemMeta().getDisplayName().replaceAll("[^0-9]", ""));
        switch (item.getType()) {
            case INK_SACK:
                if(item.getData() instanceof Dye && !((Dye) item.getData()).getColor().equals(DyeColor.GRAY))
                    apply(player, skillType, buildID);
                break;
            case ANVIL:
                edit(player, skillType, buildID);
                break;
            case FIREBALL:
                if(delete(player, skillType, buildID)) {
                    player.closeInventory();
                    player.openInventory(MenuCreator.createKitTemplate(player, skillType));
                }
                break;
        }
    }

    /**
     * Used for when a player rejoins, it will put in the exact same kit that the player
     * @see {@link #apply(Player, SkillType, int)}
     * @param player
     */
    public static void applyLastBuild(Player player) {
        String path = player.getUniqueId() + ".current";
        if(!kitConfigurator.hasPath(path)) return;
        kitConfigurator.readString(path, currentBuild -> {
            String[] split = currentBuild.split("#");
            SkillType skillType = SkillType.getByName(split[0]);
            int buildID = Integer.parseInt(split[1]);
            apply(player, skillType, buildID);
        });
    }

    /**
     * Read the build id of specific skilltype of a specific player UUID and return the
     * serialized string for skills.
     * @see {@link ChampionsPlayer#serialize()}
     *
     * @param player
     * @param skillType
     * @param buildID
     */
    private static void apply(Player player, SkillType skillType, int buildID) {
        String deserializedPlayer = getKitTable().getJSONData(player.getUniqueId(), skillType.getName(), buildID);
        if(deserializedPlayer == null) {
            player.sendMessage(ChatColor.BLUE + "Champions>" + ChatColor.GRAY + " There is no build loaded here! Click the anvil to make a kit!");
            return;
        }

        ChampionsPlayer cPlayer = ChampionsPlayerManager.getInstance().deserialize(player, deserializedPlayer);
        ChampionsPlayerManager.getInstance().addChampionsPlayer(cPlayer);
        SoundPlayer.sendSound(cPlayer.getPlayer(), "random.levelup", 0.75F, 63);
        setCurrent(player, skillType, buildID);
    }

    /**
     * Helper method to give a book the proper level of the inventory
     * As well as the description and tokens.
     * @param inventory
     * @param uuid
     * @param clasz
     * @param buildID
     * @return
     */
    private static CompletableFuture giveProperSkillLevels(final Inventory inventory, UUID uuid, String clasz, int buildID) {
        String deserial = getKitTable().getJSONData(uuid, clasz, buildID);
        if(deserial == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> {
            //vvvThis should be one function
            JsonObject object = ChampionsPlayerManager.getInstance().deserialize(deserial);
            JsonObject skillObject = object.getAsJsonObject("skills");
            Map<String, Integer> nameToLevel = new HashMap<>();
            for (String stringID : skillObject.keySet()) {
                Skill skill = InventoryData.getSkillById(Integer.valueOf(stringID));
                int level = skillObject.get(stringID).getAsInt();

                nameToLevel.put(skill.getName(), level);
            }
            //^^^^^
            ItemStack gold = inventory.getItem(8);
            int goldTokens = gold.getAmount();
            for (ItemStack book : inventory.getContents()) {
                if (book == null || book.getType() != Material.BOOK) continue;
                BookFormatter bookFormatter = InventoryData.getSkillFormatter(book);
                int level = nameToLevel.getOrDefault(bookFormatter.getName(), -1);
                if (level == -1) continue;
                book.setAmount(level);
                book.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(bookFormatter.getHeader(level));
                meta.setLore(bookFormatter.getDescription(level - 1));
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                book.setItemMeta(meta);
                goldTokens -= level * bookFormatter.getSkillTokenWeight();
            }
            if(goldTokens == 0)
                gold.setType(Material.REDSTONE);
            gold.setAmount(goldTokens);

        }, executor);
    }

    /**
     * Give the proper item levels, taking away tokens if the item is shown in the hotbar.
     * Uses the deserialized stirng to decide how much tokens to take away,
     * based on the value of each item.
     * @param inventory
     * @param skillType
     * @param uuid
     * @param buildID
     * @return
     */
    private static CompletableFuture giveProperItemLevels(final Inventory inventory, SkillType skillType, UUID uuid, int buildID) {
        String deserial = getKitTable().getJSONData(uuid, skillType.getName(), buildID);
        if(deserial == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> {
            JsonObject object = ChampionsPlayerManager.getInstance().deserialize(deserial);
            JsonObject skillObject = object.getAsJsonObject("items");
            ItemStack iron = inventory.getItem(17);
            int ironTokens = iron.getAmount();

            for (String stringID : skillObject.keySet()) {
                int itemID = skillObject.get(stringID).getAsInt();
                ChampionsItem item = ChampionsItem.getBy(itemID, skillType);
                inventory.setItem(Integer.valueOf(stringID), item.toItemStack());
                ironTokens -= item.getTokenCost();
            }
            if(ironTokens == 0)
                iron.setType(Material.REDSTONE);
            iron.setAmount(ironTokens);
        }, executor);
    }

    /**
     * Gets the basic skill selection for the skilltype.
     * Gets the basic hotbar selection for the skilltype.
     * Then adds both of the  edited skills and items via the stored strings.
     * @param player
     * @param skillType
     * @param buildID
     */
    private static void edit(Player player, SkillType skillType, int buildID) {
        buildMap.put(player.getName(), buildID);
        Inventory inventory = MenuCreator.getInv(skillType);
        ChampionsInventory.getHotbarSelection(player, skillType);

        UUID uuid = player.getUniqueId();
        String clasz = skillType.getName();

        CompletableFuture.allOf(
            giveProperSkillLevels(inventory, uuid, clasz, buildID),
            giveProperItemLevels(player.getInventory(), skillType, uuid, buildID)
        ).join();

        player.openInventory(inventory);
    }

    /**
     * When a player closes the inventory,
     * have it be applied as well as save it as the current build.
     * @param player
     * @param championsPlayer
     */
    public static void editClose(Player player, ChampionsPlayer championsPlayer) {
        UUID uuid = player.getUniqueId();
        String clasz = championsPlayer.getType().getName();
        int id = buildMap.get(player.getName());
        String data = championsPlayer.serialize().toString();
        getKitTable().set(uuid, clasz, id, data);

        setCurrent(player, championsPlayer.getType(), buildMap.get(player.getName()));
        buildMap.remove(player.getName());
    }

    /**
     * duh
     * @param player
     * @param skillType
     * @param buildID
     * @return
     */
    private static boolean delete(Player player, SkillType skillType, int buildID) {
        getKitTable().delete(player.getUniqueId(), skillType.getName(), buildID);

        return true;
    }

    /**
     * Set the current point to the deserialized string as the current build.
     * Ex: assassin#4
     * Where to find the current build would be path uuid.assassin.4
     * @param player
     * @param skillType
     * @param buildID
     */
    private static void setCurrent(Player player, SkillType skillType, int buildID) {
        kitConfigurator.set(player.getUniqueId() + ".current", skillType.getName().toLowerCase() + "#" + buildID);
        kitConfigurator.saveConfig();
    }
}
