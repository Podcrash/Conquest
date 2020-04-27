package me.raindance.champions.listeners;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.events.DeathApplyEvent;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameState;
import com.podcrash.api.listeners.ListenerBase;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.game.GameManager;
import me.raindance.champions.inventory.InvFactory;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import com.podcrash.api.mob.CustomEntityFirework;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.util.ConquestUtil;
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

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();

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
        CustomEntityFirework.spawn(player.getLocation(), effect, players.toArray(new Player[players.size()]));

        if(GameManager.getGame() != null) {
            if (GameManager.getGame().getGameState() == GameState.STARTED || GameManager.getGame().isFull()) {
                if(GameManager.getGame().isParticipating(player)) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(GameManager.getGame().getTeam(player).getSpawn(player));
                } else {
                    GameManager.addSpectator(player);
                }
            } else {
                player.setGameMode(GameMode.ADVENTURE);
                GameManager.addPlayer(player);
            }
        }
        InvFactory.applyLastBuild(player);
        if(KitPlayerManager.getInstance().getKitPlayer(player) == null) {
            ChampionsPlayer cp = ConquestUtil.defaultBuild(player);
            KitPlayerManager.getInstance().addKitPlayer(cp);
            if(!GameManager.getGame().getGameState().equals(GameState.STARTED)) GameManager.getGame().updateLobbyInventory(player);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Game game = GameManager.getGame();
        KitPlayerManager cm = KitPlayerManager.getInstance();
        KitPlayer cplayer = cm.getKitPlayer(player);
        //HitDetectionInjector.getHitDetection(e.getPlayer()).deinject();
        StatusApplier.getOrNew(player).removeStatus(Status.values());
        StatusApplier.remove(player);

        if (cplayer != null)
            cm.removeKitPlayer(cplayer);
    }

    //TODO: isnt this method pointless now? (since we aren't depending on the hub world being "world"
    @EventHandler
    public void die(DeathApplyEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(event.getDeathMessage()));
            event.getPlayer().setHealth(20.0D);
            KitPlayer cPlayer;
            if ((cPlayer = KitPlayerManager.getInstance().getKitPlayer(event.getPlayer())) != null) {
                cPlayer.restockInventory();
                cPlayer.equip();
            }
        }
    }
}