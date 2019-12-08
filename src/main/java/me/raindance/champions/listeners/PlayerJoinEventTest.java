package me.raindance.champions.listeners;

import com.podcrash.api.db.DataTableType;
import com.podcrash.api.db.PlayerTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.mc.damage.HitDetectionInjector;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.listeners.ListenerBase;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.game.GameManager;
import me.raindance.champions.inventory.InvFactory;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import com.podcrash.api.mc.mob.CustomEntityFirework;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

public class PlayerJoinEventTest extends ListenerBase {
    public PlayerJoinEventTest(JavaPlugin plugin) {
        super(plugin);
    }

    public static final ItemStack beacon = new ItemStack(Material.BEACON);

    static {
        ItemMeta meta1 = beacon.getItemMeta();
        meta1.setDisplayName(ChatColor.BOLD + ChatColor.WHITE.toString() + "DOM Game Selector");
        beacon.setItemMeta(meta1);
    }

    private void putPlayerDB(UUID uuid) {
        PlayerTable players = TableOrganizer.getTable(DataTableType.PLAYERS);
        players.insert(uuid);
    }
    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        putPlayerDB(p.getUniqueId());
        InvFactory.applyLastBuild(p);
        if(ChampionsPlayerManager.getInstance().getChampionsPlayer(e.getPlayer()) == null)
            ChampionsPlayerManager.getInstance().addChampionsPlayer(ChampionsPlayerManager.getInstance().defaultBuild(e.getPlayer()));
        if(GameManager.getGame() != null) {
            if (GameManager.getGame().isOngoing() || GameManager.getGame().isFull())
                GameManager.addSpectator(e.getPlayer());
            else {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                GameManager.addPlayer(e.getPlayer());
            }
        }
        Main.getInstance().setupPermissions(p);
        if (p.getWorld().getName().equals("world")) {
            p.getInventory().setItem(35, beacon);
            //adds the PermissionAttachment so permissions work on the players

            //Spawn the Firework, get the FireworkMeta.

            //Our random generator
            Random r = new Random();

            FireworkEffect.Type type = FireworkEffect.Type.BALL;

            //Create our effect with this
            FireworkEffect effect = FireworkEffect.builder()
                    .flicker(r.nextBoolean())
                    .withColor(Color.WHITE)
                    .with(type).trail(r.nextBoolean())
                    .build();
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            CustomEntityFirework.spawn(p.getLocation(), effect, players.toArray(new Player[players.size()]));

        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ChampionsPlayerManager cm = ChampionsPlayerManager.getInstance();
        ChampionsPlayer cplayer = cm.getChampionsPlayer(p);
        //HitDetectionInjector.getHitDetection(e.getPlayer()).deinject();
        StatusApplier.remove(e.getPlayer());
        if(!GameManager.getGame().isOngoing()) {
            GameManager.removePlayer(e.getPlayer());
        }
        if (cplayer != null)
            cm.removeChampionsPlayer(cplayer);
    }

    @EventHandler
    public void die(DeathApplyEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(event.getDeathMessage()));
            event.getPlayer().setHealth(20.0D);
            ChampionsPlayer cPlayer;
            if ((cPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(event.getPlayer())) != null) {
                cPlayer.restockInventory();
                cPlayer.equip();
            }
        }
    }
}