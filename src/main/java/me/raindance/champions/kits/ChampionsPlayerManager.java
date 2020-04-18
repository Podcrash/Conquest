package me.raindance.champions.kits;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.events.ApplyKitEvent;
import me.raindance.champions.inventory.ChampionsItem;
import me.raindance.champions.inventory.SkillData;
import me.raindance.champions.kits.classes.Duelist;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICharge;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.IInjector;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.skills.duelist.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private Map<ChampionsPlayer, List<PacketListener>> injectors = new HashMap<>();
    private Set<Integer> assassins = new HashSet<>();

    private void register(Skill skill) {
        plugin.getServer().getPluginManager().registerEvents(skill, plugin);
        skill.init();
        if (skill instanceof IPassiveTimer) ((IPassiveTimer) skill).start();
        if (skill instanceof IConstruct) ((IConstruct) skill).doConstruct();
        if (skill instanceof IInjector) addPacketListener(getChampionsPlayer(skill.getPlayer()), ((IInjector) skill).inject());
        if (skill instanceof ICharge) skill.getPlayer().sendMessage(String.format("%s%s> %sMaximum Charges: %d", ChatColor.BLUE, skill.getName(), ChatColor.GOLD, ((ICharge) skill).getMaxCharges()));
    }

    public void addChampionsPlayer(ChampionsPlayer cplayer) {
        if (cplayer == null) return;
        ChampionsPlayer oldPlayer = getChampionsPlayer(cplayer.getPlayer());
        removeChampionsPlayer(oldPlayer);

        championsPlayers.putIfAbsent(cplayer.getPlayer().getName(), cplayer);

        ChampionsPlayer cp = getChampionsPlayer(cplayer.getPlayer());
        StatusApplier.getOrNew(cp.getPlayer()).removeStatus(Status.values());
        ApplyKitEvent apply = new ApplyKitEvent(cp);
        Bukkit.getPluginManager().callEvent(apply);

        cp.equip();
        cp.heal(20);
        cp.getPlayer().setFoodLevel(20);
        cp.effects();
        for(Skill skill : cp.getSkills()) {
            skill.setPlayer(cplayer.getPlayer());
            register(skill);
        }

        if(!apply.isKeepInventory())
            cp.restockInventory();
        //cp.getPlayer().sendMessage(cp.skillsRead());
        cp.skillsRead();
    }
    public void removeChampionsPlayer(ChampionsPlayer cplayer) {
        Main.getInstance().log.info(cplayer + "");
        if (cplayer == null ||
                !championsPlayers.containsKey(cplayer.getPlayer().getName())) return;
        List<Skill> skills = cplayer.getSkills();
        Iterator<Skill> skillIterator = skills.iterator();
        Main.getInstance().getLogger().info(String.format("%s Unregistering.", cplayer.getPlayer().getName()));
        while (skillIterator.hasNext()) {
            final Skill skill = skillIterator.next();
            HandlerList.unregisterAll(skill);
            Main.getInstance().getLogger().info(String.format("%s unregistered from %s", skill.getName(), skill.getPlayer()));
            if (skill instanceof TimeResource) TimeHandler.unregister((TimeResource) skill);
            if (skill instanceof IPassiveTimer) ((IPassiveTimer) skill).stop();
        }
        clearPacketListeners(cplayer);
        cplayer.setUsesEnergy(false);
        championsPlayers.remove(cplayer.getPlayer().getName());
    }
    public void removeChampionsPlayer(Player player) {
        ChampionsPlayer championsPlayer = championsPlayers.getOrDefault(player.getName(), null);
        if(championsPlayer != null)
            removeChampionsPlayer(championsPlayer);
    }

    public ChampionsPlayer getChampionsPlayer(Player player) {
        return championsPlayers.getOrDefault(player.getName(), null);
    }

    private void addPacketListener(ChampionsPlayer cPlayer, PacketListener listener) {
        List<PacketListener> packetListeners = injectors.getOrDefault(cPlayer, new ArrayList<>());
        packetListeners.add(listener);
    }
    private void clearPacketListeners(ChampionsPlayer cPlayer) {
        List<PacketListener> packetListeners = injectors.getOrDefault(cPlayer, new ArrayList<>());
        if(packetListeners.size() == 0) return;
        ProtocolManager manager = Main.instance.getProtocolManager();
        for(PacketListener listener : packetListeners) {
            manager.removePacketListener(listener);
        }
    }
    public List<String> readSkills(String jsonStr) {
        List<String> skillWord = new ArrayList<>();
        JsonObject json = new JsonParser().parse(jsonStr).getAsJsonObject();

        JsonArray skillsJson = json.getAsJsonArray("skills");
        for (int i = 0, size = skillsJson.size(); i < size; i++) {
            int id = skillsJson.get(i).getAsInt();
            SkillData data = SkillInfo.getSkill(id);
            if(data == null) {
                System.out.println(id);
                SkillInfo.getSkills(SkillType.Druid).forEach(System.out::println);
            }else skillWord.add(String.format("%s%s%s", ChatColor.RESET, ChatColor.LIGHT_PURPLE, data.getName()));
        }
        return skillWord;
    }
    public JsonObject deserialize(String jsonStr) {
        return new JsonParser().parse(jsonStr).getAsJsonObject();
    }
    public ChampionsPlayer deserialize(Player owner, String jsonStr) {
        JsonObject json = new JsonParser().parse(jsonStr).getAsJsonObject();
        SkillType skillType = SkillType.getByName(json.get("skilltype").getAsString());

        JsonObject itemsJson = json.getAsJsonObject("items");
        ItemStack[] items = new ItemStack[9];
        for(Map.Entry<String, JsonElement> entry : itemsJson.entrySet()) {
            String slotKey = entry.getKey();
            int itemID = entry.getValue().getAsInt();
            if(itemID == -1) continue;
            ChampionsItem championsItem = ChampionsItem.getBySlotID(itemID);
            items[Integer.parseInt(slotKey)] = championsItem.toItemStack();
        }

        JsonArray skillsJson = json.getAsJsonArray("skills");
        List<Skill> skills = new ArrayList<>();

        for (int i = 0, size = skillsJson.size(); i < size; i++) {
            int id = skillsJson.get(i).getAsInt();
            SkillData data = SkillInfo.getSkill(id);

            Skill skill = data.newInstance();
            skill.setPlayer(owner);
            skills.add(skill);
        }



        ChampionsPlayer championsPlayer = newObj(owner, skills, skillType);
        championsPlayer.setDefaultHotbar(items);
        return championsPlayer; //oh god
    }

    private static final Map<SkillType, Constructor<? extends ChampionsPlayer>> constructors = new HashMap<>(); //reflection is expensive
    private Constructor<? extends ChampionsPlayer> getConstructor(SkillType skillType) {
        if(skillType == SkillType.Global) throw new IllegalArgumentException("Global is not allowed");
        if(!constructors.containsKey(skillType)) {
            try {
                Class<? extends ChampionsPlayer> clazz = (Class<? extends ChampionsPlayer>) Class.forName("me.raindance.champions.kits.classes." + skillType.getName());
                Constructor cons = clazz.getDeclaredConstructor(Player.class, List.class);
                constructors.put(skillType, cons);
            }catch (ClassNotFoundException|NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return constructors.get(skillType);
    }

    public void clear(){
        Iterator iterator = championsPlayers.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    public  <T extends ChampionsPlayer> T newObj(Player owner, List<Skill> skills, SkillType skillType) {
        try {
            return (T) getConstructor(skillType).newInstance(owner, skills);
        }catch (InstantiationException|IllegalAccessException|InvocationTargetException e){
            e.printStackTrace();
        }
        throw new IllegalArgumentException("something went wrong ya");
    }

    public ChampionsPlayer defaultBuild(Player player) {
        List<Skill> skills = new ArrayList<>();
        skills.add(new Riposte());
        skills.add(new Lunge());
        skills.add(new Revenge());
        skills.add(new FatalStrike());
        skills.add(new Challenger());

        Duelist knight = new Duelist(player, skills);
        return knight;
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
