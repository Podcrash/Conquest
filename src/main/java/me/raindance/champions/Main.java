package me.raindance.champions;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.netty.util.NetUtil;
import me.lordraindance2.sweetdreams.LunarDance;
import me.raindance.champions.commands.*;
import me.raindance.champions.damage.DamageQueue;
import me.raindance.champions.damage.HitDetectionInjector;
import me.raindance.champions.db.DataTableType;
import me.raindance.champions.db.PlayerPermissionsTable;
import me.raindance.champions.db.TableOrganizer;
import me.raindance.champions.disguise.Disguiser;
import me.raindance.champions.effect.particle.ParticleRunnable;
import me.raindance.champions.events.TickEvent;
import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import me.raindance.champions.game.GameType;
import me.raindance.champions.inventory.InvFactory;
import me.raindance.champions.inventory.InventoryData;
import me.raindance.champions.inventory.update.InventoryUpdater;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.items.ItemHelper;
import me.raindance.champions.listeners.*;
import me.raindance.champions.listeners.maintainers.GameListener;
import me.raindance.champions.listeners.maintainers.MapMaintainListener;
import me.raindance.champions.listeners.maintainers.SkillMaintainListener;
import me.raindance.champions.mob.CustomEntityType;
import me.raindance.champions.redis.Communicator;
import me.raindance.champions.util.PlayerCache;
import me.raindance.champions.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
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
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static volatile Main instance;
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
    private Map<String, Configurator> configurators = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(8);
    private LunarDance antiCheat;

    private CompletableFuture<Void> registerMessengers() {
        return CompletableFuture.runAsync(() -> {
            getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);
            getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, new MessageListener(CHANNEL_NAME));
        }, executor);
    }
    private CompletableFuture<Void> registerListeners() {
        return CompletableFuture.runAsync(() -> {
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
        return CompletableFuture.runAsync(() -> {
            InventoryData.addAssassin();
            InventoryData.addRanger();
            InventoryData.addMage();
            InventoryData.addKnight();
            InventoryData.addBrute();
            InventoryData.addGlobal();
        }, executor);
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
    private CompletableFuture<Void> startSQLBases() {
        TableOrganizer.createTables(false);
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public void onEnable() {
        /*
        getConfig().options().copyDefaults(true);
        saveConfig();
        */
        instance = this;

        log.info("[GameManager] Making a lot of games");
        Game game = GameManager.createGame(Long.toString(System.currentTimeMillis()), GameType.DOM);
        log.info("Created game " + game.getName());

        CompletableFuture communications = Communicator.setup(executor);
        CompletableFuture sql = startSQLBases();
        CompletableFuture kb = setKnockback();
        CompletableFuture customEnchantment = registerCustomEnchant();
        CompletableFuture listeners = registerListeners();
        CompletableFuture commands = registerCommands();
        CompletableFuture injectors = registerInjectors();
        CompletableFuture setups = setUp();
        CompletableFuture setupClasses = setUpClasses();
        CompletableFuture msgs = registerMessengers();

        antiCheat = new LunarDance();
        antiCheat.setup(this);

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

        CompletableFuture.allOf(
            sql,
            communications,
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
        DamageQueue.active = true;
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new DamageQueue(), 0L, 1L);
        Bukkit.getScheduler().runTaskTimer(Main.instance, new InventoryUpdater(), 0L, 1L);

        //This part is really only used for reloading
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if(players.size() > 0) {
            for(Player p : players) {
                HitDetectionInjector detection = new HitDetectionInjector(p);
                detection.injectHitDetection();
                InvFactory.applyLastBuild(p);
            }
        }
        executor.shutdown();
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
        ChampionsPlayerManager.getInstance().clear();
        antiCheat.disable();
        //CustomEntityType.unregisterEntities();

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
            getCommand("kb").setExecutor(new KnockbackCommand());
            getCommand("hitreg").setExecutor(new HitRegCommand());
            getCommand("kill").setExecutor(new KillCommand());
            getCommand("tell").setExecutor(new TellCommand());

        }, executor);
    }
    public void setupPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(this);
        this.playerPermissions.put(player.getUniqueId(), attachment);
        permissionsSetter(player);
    }
    private void permissionsSetter(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            PermissionAttachment attachment = this.playerPermissions.get(player.getUniqueId());
            PlayerPermissionsTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
            List<Perm> perms = table.getRoles(player.getUniqueId());
            for(Perm perm : Perm.values()) {
                boolean a = false;
                if(perms.contains(perm)) {
                    player.sendMessage(String.format("%sYou have been assigned the %s role!", ChatColor.GREEN, perm.name()));
                    a = true;
                }
                for(String permission : perm.getPermissions()) {
                    attachment.setPermission(permission, a);
                }
            }
            String[] disallowedPerms = new String[] {
                    "bukkit.command.reload",
                    "bukkit.command.timings",
                    "bukkit.command.plugins",
                    "bukkit.command.help",
                    "bukkit.command.ban-ip",
                    "bukkit.command.stop",
            };
            Main.getInstance().getLogger().info("Disabling bad permissions");
            for(String disallowed : disallowedPerms)
                attachment.setPermission(disallowed, false);
        });
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
