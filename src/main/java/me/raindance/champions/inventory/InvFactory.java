package me.raindance.champions.inventory;


import com.google.gson.JsonObject;
import me.raindance.champions.Configurator;
import me.raindance.champions.Main;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.classes.*;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.sound.SoundPlayer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public final class InvFactory {
    private static Map<String, Integer> buildMap = new HashMap<>();
    private static final Configurator kitConfigurator = Main.getConfigurator("kits");
    private InvFactory() {

    }

    private static List<Skill> convertInventoryToSkills(Player player, Inventory menu) {
        List<Skill> skills = new ArrayList<>();
        for (ItemStack book : menu.getContents()) {
            if(book == null) continue;
            if (book.containsEnchantment(Main.customEnchantment)) {
                Skill skill = InventoryData.getSkill(book);
                if (skill != null) {
                    try {
                        BookFormatter formatter = InventoryData.getSkillFormatter(skill);
                        Object _skill = formatter.getConstructor().newInstance(player, book.getAmount());
                        if (_skill instanceof Skill) {
                            skills.add((Skill) _skill);
                        } else player.sendMessage("Not a valid skill or somethin");
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException ee) {
                        ee.printStackTrace();
                    }
                } else player.sendMessage("Error on making skill");
            }
        }
        return skills;
    }
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

    public static ChampionsPlayer inventoryToChampion(Player player, Inventory menu, SkillType skillType) {
        List<Skill> skills = convertInventoryToSkills(player, menu);
        removeDuplicatedInvTypes(skills);
        ChampionsPlayer newPlayer = findViaSkillType(skillType, player, skills);
        newPlayer.setDefaultHotbar();

        String serial = newPlayer.serialize().toString();
        Main.getInstance().getLogger().info("[InvFactory] " + serial);
        Main.getInstance().getLogger().info("[InvFactory] Going to try deserializing this (yikes)");
        try {
            ChampionsPlayer cPlayer = ChampionsPlayerManager.getInstance().deserialize(player, serial);
            Main.getInstance().getLogger().info("[InvFactory] " + cPlayer.serialize().toString());
            if(serial.equals(cPlayer.serialize().toString()))
                Main.getInstance().getLogger().info("Mission Accomplished");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return newPlayer;
    }

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
    private static void apply(Player player, SkillType skillType, int buildID) {
        String path = player.getUniqueId() + "." + skillType.getName().toLowerCase() + "." + buildID;
        if(kitConfigurator.hasPath(path))
            kitConfigurator.readString(path, deserializedPlayer -> {
                try {
                    ChampionsPlayer cPlayer = ChampionsPlayerManager.getInstance().deserialize(player, deserializedPlayer);
                    Main.getInstance().getLogger().info(cPlayer.serialize().toString());
                    ChampionsPlayer oldPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
                    ChampionsPlayerManager.getInstance().removeChampionsPlayer(player);
                    ChampionsPlayerManager.getInstance().addChampionsPlayer(cPlayer);
                    cPlayer.setSpawnLocation(oldPlayer.getSpawnLocation());
                    cPlayer.restockInventory();
                    SoundPlayer.sendSound(cPlayer.getPlayer(), "random.levelup", 0.75F, 63);
                }catch (Exception e){
                    player.sendMessage("Champions> Something went wrong with building your kit");
                    e.printStackTrace();
                }
            });
        else player.sendMessage("There is no build loaded here! Click the anvil to make a kit!");
    }

    private static void giveProperSkillLevels(final Inventory inventory, String path) {
        if(!kitConfigurator.hasPath(path)) return;
        try {
            kitConfigurator.readString(path, (deserial) -> {
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
                    book.addEnchantment(Main.customEnchantment, 1);
                    goldTokens -= level;
                }
                if(goldTokens == 0)  {
                    gold.setType(Material.REDSTONE);
                }
                gold.setAmount(goldTokens);

            }).get();
        }catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void giveProperItemLevels(final Inventory inventory, SkillType skillType, String path) {
        if(!kitConfigurator.hasPath(path)) return;
        try {
            kitConfigurator.readString(path, (deserial) -> {
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

                if(ironTokens == 0)  {
                    iron.setType(Material.REDSTONE);
                }
                iron.setAmount(ironTokens);

            }).get();
        }catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }
    private static void edit(Player player, SkillType skillType, int buildID) {
        buildMap.put(player.getName(), buildID);
        Inventory inventory = MenuCreator.getInv(skillType);
        ChampionsInventory.getHotbarSelection(player, skillType);
        String path = player.getUniqueId() + "." + skillType.getName().toLowerCase() + "." + buildID;
        giveProperSkillLevels(inventory, path);
        giveProperItemLevels(player.getInventory(), skillType, path);
        player.openInventory(inventory);
    }
    public static void editClose(Player player, ChampionsPlayer championsPlayer) {
        kitConfigurator.getConfig().set(player.getUniqueId() + "." +
                        championsPlayer.getType().getName().toLowerCase() + "." +
                        buildMap.get(player.getName()), championsPlayer.serialize().toString());
        buildMap.remove(player.getName());
        kitConfigurator.saveConfig();
    }
    private static boolean delete(Player player, SkillType skillType, int buildID) {
        kitConfigurator.deletePath(player.getUniqueId() + "." + skillType.getName().toLowerCase() + "." + Integer.toString(buildID));
        kitConfigurator.saveConfig();
        return true;
    }

}
