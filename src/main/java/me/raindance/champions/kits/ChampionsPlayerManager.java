package me.raindance.champions.kits;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.raindance.champions.Main;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.inventory.BookFormatter;
import me.raindance.champions.inventory.ChampionsItem;
import me.raindance.champions.inventory.InventoryData;
import me.raindance.champions.kits.classes.Assassin;
import me.raindance.champions.kits.classes.Mage;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IConstruct;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ChampionsPlayerManager {
    private static volatile ChampionsPlayerManager cpm;
    private JavaPlugin plugin = Main.getInstance();
    private HashMap<String, ChampionsPlayer> championsPlayers = new HashMap<>();
    private Set<Integer> assassins = new HashSet<>();

    public void addChampionsPlayer(ChampionsPlayer cplayer) {
        if (cplayer == null) return;
        championsPlayers.putIfAbsent(cplayer.getPlayer().getName(), cplayer);
        ChampionsPlayer cp = getChampionsPlayer(cplayer.getPlayer());
        if(!cp.equip()) {
            cp.getPlayer().sendMessage("Something went wrong?");
            return;
        }
        StatusApplier.getOrNew(cplayer.getPlayer()).removeStatus(Status.values());
        List<Skill> skills = cp.getSkills();
        cplayer.getPlayer().setFoodLevel(20);
        cplayer.getPlayer().sendMessage(String.format("You are a %s", cplayer.getName()));
        if (cplayer instanceof Assassin) {
            StatusApplier.getOrNew(cplayer.getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1);
        } else if (cplayer instanceof Mage) {
            cplayer.setUsesEnergy(true);
        }

        for(Skill skill : skills) {
            if(skill.getInvType().equals(InvType.SWORD)) {
                if(cplayer.getPlayer().getInventory().contains(Material.GOLD_SWORD)) skill.setBoosted(true);
            }else if(skill.getInvType().equals(InvType.AXE)) {
                if(cplayer.getPlayer().getInventory().contains(Material.GOLD_AXE)) skill.setBoosted(true);
            }
            cplayer.getPlayer().sendMessage(String.format("You have registered %s %d", skill.getName(), skill.getLevel()));
            plugin.getServer().getPluginManager().registerEvents(skill, plugin);
            if (skill instanceof IPassiveTimer) ((IPassiveTimer) skill).start();
            if (skill instanceof IConstruct) ((IConstruct) skill).doConstruct();
        }

    }
    public void removeChampionsPlayer(final ChampionsPlayer cplayer) {
        if (!championsPlayers.containsKey(cplayer.getPlayer().getName()) && !championsPlayers.containsValue(cplayer)) return;
        List<Skill> skills = cplayer.getSkills();
        Iterator<Skill> skillIterator = skills.iterator();
        Main.getInstance().getLogger().info(String.format("%s Unregistering.", cplayer.getPlayer().getName()));
        while (skillIterator.hasNext()) {
            final Skill skill = skillIterator.next();
            HandlerList.unregisterAll(skill);
            Main.getInstance().getLogger().info(String.format("%s unregistered from %s", skill.getName(), skill.getPlayer()));
            skill.setValid(false);
            if (skill instanceof TimeResource) {
                TimeHandler.unregister((TimeResource) skill);
            }
        }
        championsPlayers.remove(cplayer.getPlayer().getName());
        if (cplayer instanceof Assassin) {
            StatusApplier.getOrNew(cplayer.getPlayer()).removeStatus(Status.SPEED);
        } else if (cplayer instanceof Mage){
            cplayer.setUsesEnergy(false);
            System.out.println("removed your energy bar!");
        }

    }
    public void removeChampionsPlayer(Player player) {
        ChampionsPlayer championsPlayer = championsPlayers.getOrDefault(player.getName(), null);
        if(championsPlayer != null)
            removeChampionsPlayer(championsPlayer);
    }

    public ChampionsPlayer getChampionsPlayer(Player player) {
        return championsPlayers.getOrDefault(player.getName(), null);
    }

    public List<String> readSkills(String jsonStr) {
        List<String> skillWord = new ArrayList<>();
        JsonObject json = new JsonParser().parse(jsonStr).getAsJsonObject();

        JsonObject skillsJson = json.getAsJsonObject("skills");
        for (String idKey : skillsJson.keySet()) {
            Skill skill = InventoryData.getSkillById(Integer.parseInt(idKey));
            skillWord.add(skill.getName() + ": " + skillsJson.get(idKey).getAsInt());
        }
        return skillWord;
    }
    public JsonObject deserialize(String jsonStr) {
        return new JsonParser().parse(jsonStr).getAsJsonObject();
    }
    public ChampionsPlayer deserialize(Player owner, String jsonStr) throws Exception {
        JsonObject json = new JsonParser().parse(jsonStr).getAsJsonObject();
        SkillType skillType = SkillType.getByName(json.get("skilltype").getAsString());

        JsonObject skillsJson = json.getAsJsonObject("skills");
        List<Skill> skills = new ArrayList<>();

        for (String idKey : skillsJson.keySet()) {
            Skill skill = InventoryData.getSkillById(Integer.parseInt(idKey));
            BookFormatter book = InventoryData.getSkillFormatter(skill);
            Skill newSkill = (Skill) book.getConstructor().newInstance(owner, skillsJson.get(idKey).getAsInt());
            skills.add(newSkill);
        }

        JsonObject itemsJson = json.getAsJsonObject("items");
        ItemStack[] items = new ItemStack[9];
        for(String slotKey : itemsJson.keySet()) {
            int itemID = itemsJson.get(slotKey).getAsInt();
            if(itemID == -1) continue;
            ChampionsItem championsItem = ChampionsItem.getBy(itemID, skillType);
            items[Integer.parseInt(slotKey)] = championsItem.toItemStack();
        }

        ChampionsPlayer championsPlayer = newObj(owner, skills, skillType);
        championsPlayer.setDefaultHotbar(items);
        return championsPlayer; //oh god
    }

    private static final Map<SkillType, Constructor> constructors = new HashMap<>(); //reflection is expensive
    private Constructor getConstructor(SkillType skillType) {
        if(skillType == SkillType.Global) throw new IllegalArgumentException("Global is not allowed");
        if(!constructors.containsKey(skillType)) {
            try {
                Class<ChampionsPlayer> clazz = (Class<ChampionsPlayer>) Class.forName("me.raindance.champions.kits.classes." + skillType.getName());
                Constructor cons = clazz.getDeclaredConstructor(Player.class, List.class);
                constructors.put(skillType, cons);
            }catch (ClassNotFoundException|NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return constructors.get(skillType);
    }

    private ChampionsPlayer newObj(Player owner, List<Skill> skills, SkillType skillType) {
        try {
            return (ChampionsPlayer) getConstructor(skillType).newInstance(owner, skills);
        }catch (InstantiationException|IllegalAccessException|InvocationTargetException e){
            e.printStackTrace();
        }
        throw new IllegalArgumentException("something went wrong ya");
    }

    public HashMap getChampionsPlayers() {
        return championsPlayers;
    }

    public static ChampionsPlayerManager getInstance() {
        if (cpm == null) {
            synchronized (ChampionsPlayerManager.class) {
                if (cpm == null) {
                    cpm = new ChampionsPlayerManager();
                }
            }

        }
        return cpm;
    }


    private ChampionsPlayerManager() {

    }
}
