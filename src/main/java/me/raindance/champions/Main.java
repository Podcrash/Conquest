package me.raindance.champions;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.raindance.champions.commands.*;
import me.raindance.champions.damage.DamageQueue;
import me.raindance.champions.damage.HitDetectionInjector;
import me.raindance.champions.disguise.Disguiser;
import me.raindance.champions.effect.particle.ParticleRunnable;
import me.raindance.champions.events.TickEvent;
import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import me.raindance.champions.game.GameType;
import me.raindance.champions.inventory.InventoryData;
import me.raindance.champions.inventory.update.InventoryUpdater;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.classes.Mage;
import me.raindance.champions.kits.items.ItemHelper;
import me.raindance.champions.kits.skills.BruteSkills.WhirlwindAxe;
import me.raindance.champions.kits.skills.MageSkills.ArticArmor;
import me.raindance.champions.kits.skills.RangerSkills.Sharpshooter;
import me.raindance.champions.kits.skills.RangerSkills.WolfsPounce;
import me.raindance.champions.listeners.*;
import me.raindance.champions.listeners.maintainers.GameListener;
import me.raindance.champions.listeners.maintainers.MapMaintainListener;
import me.raindance.champions.listeners.maintainers.SkillMaintainListener;
import me.raindance.champions.mob.CustomEntityType;
import me.raindance.champions.util.BlankEnchantment;
import me.raindance.champions.util.PlayerCache;
import me.raindance.champions.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static volatile Main instance;
    private ProtocolManager protocolManager;
    private BukkitTask tickTask;
    public static BlankEnchantment customEnchantment = new BlankEnchantment();
    public Logger log = this.getLogger();
    //Mapping configuration files
    private File mapConfig;
    private FileConfiguration mapConfiguration;
    // permissions HashMap
    private Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();
    //configurators
    private Map<String, Configurator> configurators = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(8);

    private Callable<Void> registerListeners() {
        return () -> {
            new GameDamagerConverterListener(this);
            new GameListener(this);
            new InventoryListener(this);
            new MapListener(this);
            new ObjectiveListener(this);
            new MapMaintainListener(this);
            new PlayerJoinEventTest(this);
            new SkillMaintainListener(this);
            new ItemHelper(this);
            new TickEventListener(this);
            new Disguiser().disguiserIntercepter();
            return null;
        };
    }
    private Callable<Void> setUpClasses() {
        return () -> {
            InventoryData.addAssassin();
            InventoryData.addRanger();
            InventoryData.addMage();
            InventoryData.addKnight();
            InventoryData.addBrute();
            InventoryData.addGlobal();
            return null;
        };
    }
    private Callable<Void> registerInjectors() {
        return () -> {
            //new SoundInjector();
            return null;
        };
    }
    private Callable<Void> setUp() {
        return  () -> {
            final PluginManager pman = Bukkit.getPluginManager();
            tickTask = Bukkit.getScheduler().runTaskTimer(instance, () -> pman.callEvent(new TickEvent()), 1L, 1L);

            return null;
        };
    }
    private Callable<Void> registerCustomEnchant(){
        return  () -> {
            //from https://bukkit.org/threads/custom-enchantments-you-say-what.160684/
            try {
                Field byIdField = Enchantment.class.getDeclaredField("byId");
                Field byNameField = Enchantment.class.getDeclaredField("byName");

                byIdField.setAccessible(true);
                byNameField.setAccessible(true);

                @SuppressWarnings("unchecked")
                HashMap<Integer, Enchantment> byId = (HashMap<Integer, Enchantment>) byIdField.get(null);
                @SuppressWarnings("unchecked")
                HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) byNameField.get(null);

                if (byId.containsKey(customEnchantment.getId()))
                    byId.remove(customEnchantment.getId());

                if (byName.containsKey(customEnchantment.getName()))
                    byName.remove(customEnchantment.getName());

                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Enchantment.registerEnchantment(customEnchantment);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                //if this is thrown it means the id is already taken.
            }
            return null;
        };
    }

    @Override
    public void onEnable() {
        /*
        getConfig().options().copyDefaults(true);
        saveConfig();
        */
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        mapConfig = new File(getDataFolder(), "maps.yml");
        mapConfiguration = YamlConfiguration.loadConfiguration(mapConfig);
        saveMapConfig();
        configurators.put("kits", new Configurator(this, "kits"));
        List<String> domMaps = new ArrayList<>();
        domMaps.add("Sakura");
        domMaps.add("Delphic");
        domMaps.add("Pinewood");
        domMaps.add("Gulley");
        getConfig().set("worlds", domMaps);
        saveConfig();

        WorldManager.getInstance().loadWorlds();
        this.log.info(Bukkit.getWorlds().toString());
        CustomEntityType.registerEntities();
        try {
            executor.invokeAll(Arrays.asList(
                    setKnockback(),
                    registerCustomEnchant(),
                    registerListeners(),
                    registerCommands(),
                    registerInjectors(),
                    setUp(),
                    setUpClasses()));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("[GameManager] Making a lot of games");
        for(int i = 0; i < 9; i++) {
            Game game = GameManager.createGame(Long.toString(System.currentTimeMillis()), GameType.DOM);
            log.info("Created game " + game.getName());
        }
        ParticleRunnable.start();
        PlayerCache.packetUpdater();
        DamageQueue.active = true;
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new DamageQueue(), 0L, 1L);
        Bukkit.getScheduler().runTaskTimer(Main.instance, new InventoryUpdater(), 0L, 1L);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Main.getInstance().getLogger().info(players.toString());
        if(players.size() > 0) {
            for(Player p : players) {
                new HitDetectionInjector(p).injectHitDetection();
                WolfsPounce evade = new WolfsPounce(p, 5);
                ArticArmor articArmor = new ArticArmor(p, 3);
                Sharpshooter backstab = new Sharpshooter(p, 3);
                WhirlwindAxe axe = new WhirlwindAxe(p, 5);
                axe.setBoosted(true);

                List<Skill> rangerKit = Arrays.asList(backstab, articArmor, axe, evade);
                ChampionsPlayerManager.getInstance().addChampionsPlayer(new Mage(p, rangerKit));
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLoad() {
    }
    @Override
    public void onDisable() {
        DamageQueue.active = false;
        WorldManager.getInstance().unloadWorlds();
        tickTask.cancel();
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        Bukkit.getScheduler().cancelAllTasks();
        //CustomEntityType.unregisterEntities();

    }

    private Callable<Void> setKnockback() {
        return () -> {
            log.info("Kb Numbers: ");
        /*

        getDouble("settings.knockback.friction", knockbackFriction);
        getDouble("settings.knockback.horizontal", knockbackHorizontal);
        getDouble("settings.knockback.vertical", knockbackVertical);
        getDouble("settings.knockback.verticallimit", knockbackVerticalLimit);
        getDouble("settings.knockback.extrahorizontal", knockbackExtraHorizontal);
        getDouble("settings.knockback.extravertical", knockbackExtraVertical);
         */

            SpigotConfig.knockbackFriction = SpigotConfig.config.getDouble("settings.knockback.friction");
            SpigotConfig.knockbackHorizontal = SpigotConfig.config.getDouble("settings.knockback.horizontal");
            SpigotConfig.knockbackVertical = SpigotConfig.config.getDouble("settings.knockback.vertical");
            SpigotConfig.knockbackVerticalLimit = SpigotConfig.config.getDouble("settings.knockback.verticallimit");
            SpigotConfig.knockbackExtraHorizontal = SpigotConfig.config.getDouble("settings.knockback.extrahorizontal");
            SpigotConfig.knockbackExtraVertical = SpigotConfig.config.getDouble("settings.knockback.extravertical");


            log.info("Friction: " + SpigotConfig.knockbackFriction);
            log.info("Horizontal: " + SpigotConfig.knockbackHorizontal);
            log.info("Veritcal: " + SpigotConfig.knockbackVertical);
            log.info("Vertical Limit: " + SpigotConfig.knockbackVerticalLimit);
            log.info("Extra Horizontal: " + SpigotConfig.knockbackExtraHorizontal);
            log.info("Extra Vertical: " + SpigotConfig.knockbackExtraVertical);

            return null;
        };
    }

    public FileConfiguration getMapConfiguration() {
        return mapConfiguration;
    }
    public void saveMapConfig() {
        try {
            mapConfiguration.save(mapConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Callable<Void> registerCommands() {
        return () -> {
            getCommand("join").setExecutor(new JoinCommand());
            getCommand("leave").setExecutor(new LeaveCommand());
            getCommand("team").setExecutor(new TeamCommand());
            getCommand("wteleport").setExecutor(new WorldTeleportCommand());
            getCommand("start").setExecutor(new StartCommand());
            getCommand("end").setExecutor(new EndCommand());
            getCommand("setmap").setExecutor(new SetMapCommand());
            getCommand("invis").setExecutor(new InvisCommand());
            getCommand("damage").setExecutor(new DamageCommand());
            getCommand("velo").setExecutor(new VelocityCommand());
            getCommand("disguise").setExecutor(new DisguiseCommand());
            getCommand("currentlocation").setExecutor(new CurrentLocationCommand());
            getCommand("copyworld").setExecutor(new CopyWorldCommand());
            getCommand("deleteworld").setExecutor(new DeleteWorldCommand());
            getCommand("ping").setExecutor(new PingCommand());
            getCommand("mapinfo").setExecutor(new InfoMapCommand());
            getCommand("rc").setExecutor(new ReloadChampionsCommand());
            getCommand("spec").setExecutor(new SpecCommand());
            getCommand("view").setExecutor(new ViewCommand());
            getCommand("skill").setExecutor(new SkillCommand());
            getCommand("setrole").setExecutor(new SetRoleCommand());
            getCommand("newgame").setExecutor(new NewGameCommand());
            getCommand("kb").setExecutor(new KnockbackCommand());

            return null;
        };
    }
    public void setupPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(this);
        this.playerPermissions.put(player.getUniqueId(), attachment);
        permissionsSetter(player.getUniqueId());
    }
    private void permissionsSetter(UUID uuid) {
        PermissionAttachment attachment = this.playerPermissions.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        for(String roles : this.getConfig().getConfigurationSection("roles").getKeys(false)) {
            if(getConfig().getStringList("roles." + roles + ".players").contains(player.getName())) {
                player.sendMessage(String.format("%sYou have been assigned the %s role!", ChatColor.GREEN, roles));
                for(String permissions : this.getConfig().getStringList("roles." + roles + ".permissions")) {
                    attachment.setPermission(permissions, true);
                }
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }
    public ProtocolManager getProtocolManager() {
        return (protocolManager != null) ? protocolManager : ProtocolLibrary.getProtocolManager();
    }

    public static Configurator getConfigurator(String name) {
        return instance.configurators.get(name);
    }
}
