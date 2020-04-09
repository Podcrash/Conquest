package me.raindance.champions;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.mc.damage.DamageQueue;
import com.podcrash.api.mc.damage.HitDetectionInjector;
import com.podcrash.api.mc.disguise.Disguiser;
import com.podcrash.api.mc.effect.particle.ParticleRunnable;
import com.podcrash.api.mc.events.TickEvent;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.mc.util.ConfigUtil;
import com.podcrash.api.mc.util.PlayerCache;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.db.redis.Communicator;
import me.raindance.champions.commands.*;
import me.raindance.champions.game.DomGame;
import me.raindance.champions.inventory.InvFactory;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.SkillInfo;
import me.raindance.champions.kits.itemskill.ItemHelper;
import me.raindance.champions.listeners.*;
import me.raindance.champions.listeners.maintainers.ApplyKitListener;
import me.raindance.champions.listeners.maintainers.DomGameListener;
import me.raindance.champions.listeners.maintainers.SkillMaintainListener;
import me.raindance.champions.listeners.maintainers.SoundDamage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static volatile Main instance;
    private static Set<String> defaultAllowedSkills;
    private Properties properties;

    public static final String CHANNEL_NAME = "Champions";
    private ProtocolManager protocolManager;
    private BukkitTask tickTask;
    public Logger log = this.getLogger();
    //Mapping configuration files
    private File mapConfig;
    private FileConfiguration mapConfiguration;
    // permissions HashMap
    private Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();
    //configurators
    private ExecutorService executor = Executors.newFixedThreadPool(8);

    private CompletableFuture<Void> registerMessengers() {
        return CompletableFuture.runAsync(() -> {
            getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);
            getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, new MessageListener(CHANNEL_NAME));
        }, executor);
    }
    private CompletableFuture<Void> registerListeners() {
        return CompletableFuture.runAsync(() -> {
            new DomGameListener(this);
            new SoundDamage(this);
            new InventoryListener(this);
            new MapListener(this);
            new ObjectiveListener(this);
            new PlayerJoinEventTest(this);
            new SkillMaintainListener(this);
            new ItemHelper(this);
            new TickEventListener(this);
            new Disguiser().disguiserIntercepter();
            new ApplyKitListener(this);
            new EconomyListener(this);

            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void overrideStop(PlayerCommandPreprocessEvent event) {
                    if(event.getMessage().split(" ")[0].equalsIgnoreCase("/stop"))
                        event.setCancelled(true);
                }
            }, this);
        }, executor);
    }
    private CompletableFuture<Void> setUpClasses() {
        return CompletableFuture.runAsync(SkillInfo::setUp, executor);
    }
    private CompletableFuture<Void> registerInjectors() {
        return CompletableFuture.runAsync(() -> {
            //new SoundInjector();
        }, executor);
    }
    private CompletableFuture<Void> setUp() {
        return CompletableFuture.runAsync(() -> {
            final PluginManager pman = Bukkit.getPluginManager();
            tickTask = Bukkit.getScheduler().runTaskTimer(instance, () -> pman.callEvent(new TickEvent()), 1L, 1L);

        }, executor);
    }
    private CompletableFuture<Void> registerCustomEnchant(){
        return CompletableFuture.runAsync(() -> {
            // lol
        }, executor);
    }

    public static Set<String> getDefaultAllowedSkills() {
        return defaultAllowedSkills;
    }

    @Override
    public void onEnable() {
        /*
        getConfig().options().copyDefaults(true);
        saveConfig();
        */
        instance = this;

        this.properties = ConfigUtil.readPropertiesFile(getClass().getClassLoader(), "allowed.properties");
        //set allowed skills
        String raw = (String) this.properties.get("allowed");
        List<String> t = new ArrayList<>();
        if(raw != null) {
            for (String r : raw.split(",")) {
                String p = ChatUtil.strip(r);
                t.add(p);
            }
            defaultAllowedSkills = new HashSet<>(t);
        }
        //set config stuff
        PodcrashSpigot spigot = Pluginizer.getSpigotPlugin();
        spigot.registerConfigurator("kits");
        spigot.registerConfigurator("skilldescriptions");

        DomGame game = new DomGame(GameManager.getCurrentID(), Long.toString(System.currentTimeMillis()));
        GameManager.createGame(game);
        log.info("Created game " + game.getName());

        CompletableFuture kb = setKnockback();
        CompletableFuture customEnchantment = registerCustomEnchant();
        CompletableFuture listeners = registerListeners();
        CompletableFuture commands = registerCommands();
        CompletableFuture injectors = registerInjectors();
        CompletableFuture setups = setUp();
        CompletableFuture setupClasses = setUpClasses();
        CompletableFuture msgs = registerMessengers();

        spigot.getWorldSetter().loadFromEnvVariable("conquest_spawn");

        protocolManager = ProtocolLibrary.getProtocolManager();
        mapConfig = new File(getDataFolder(), "maps.yml");
        mapConfiguration = YamlConfiguration.loadConfiguration(mapConfig);
        saveMapConfig();

        this.log.info(Bukkit.getWorlds().toString());

        CompletableFuture allFutures = CompletableFuture.allOf(
            kb,
            customEnchantment,
            listeners,
            commands,
            injectors,
            setups,
            setupClasses,
            msgs
        );

        ParticleRunnable.start();
        PlayerCache.packetUpdater();

        try {
            allFutures.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //This part is really only used for reloading
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if(players.size() > 0) {
            for(Player p : players) {
                HitDetectionInjector detection = new HitDetectionInjector(p);
                detection.injectHitDetection();
                InvFactory.applyLastBuild(p);
            }
        }
        Communicator.putLobbyMap("maxsize", GameManager.getGame().getMaxPlayers());
        executor.shutdown();
    }

    @Override
    public void onDisable() {
        DamageQueue.active = false;
        tickTask.cancel();
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        Bukkit.getScheduler().cancelAllTasks();
        ChampionsPlayerManager.getInstance().clear();
        //CustomEntityType.unregisterEntities();
        GameManager.destroyCurrentGame();

    }

    private CompletableFuture<Void> setKnockback() {
        return CompletableFuture.runAsync(() -> {
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

        }, executor);
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

    private CompletableFuture<Void> registerCommands() {
        return CompletableFuture.runAsync(() -> {
            getCommand("leave").setExecutor(new LeaveCommand());
            getCommand("wteleport").setExecutor(new WorldTeleportCommand());
            getCommand("invis").setExecutor(new InvisCommand());
            getCommand("damage").setExecutor(new DamageCommand());
            getCommand("velo").setExecutor(new VelocityCommand());
            getCommand("disguise").setExecutor(new DisguiseCommand());
            getCommand("currentlocation").setExecutor(new CurrentLocationCommand());
            getCommand("copyworld").setExecutor(new CopyWorldCommand());
            getCommand("deleteworld").setExecutor(new DeleteWorldCommand());
            getCommand("rc").setExecutor(new ReloadChampionsCommand());
            getCommand("skill").setExecutor(new SkillCommand());
            getCommand("lock").setExecutor(new LockCommand());
        }, executor);
    }

    public static Main getInstance() {
        return instance;
    }
    public ProtocolManager getProtocolManager() {
        return (protocolManager != null) ? protocolManager : ProtocolLibrary.getProtocolManager();
    }
}
