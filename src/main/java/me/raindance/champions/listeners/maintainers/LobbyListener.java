package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LobbyListener extends ListenerBase {


    public LobbyListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLobbyDeath(DeathApplyEvent event) {
        Game game = GameManager.getGame();
        Player p = event.getPlayer();

        if (game.getGameState() != GameState.LOBBY) return;

        ChampionsPlayer champion = ChampionsPlayerManager.getInstance().getChampionsPlayer(p);
        champion.resetCooldowns();
        List<Skill> skills = ChampionsPlayerManager.getInstance().getChampionsPlayer(p).getSkills();
        for(Skill skill : skills) {
            if(!(skill instanceof TogglePassive)) continue;
            if (((TogglePassive) skill).isToggled())
                ((TogglePassive) skill).forceToggle();
        }

        event.setCancelled(true);
    }

    /**
     * Enable lobby pvp for the player by removing invincibility and applying their kit
     * @param p - the player
     */
    private void enableGameLobbyPVP(Player p) {
        Game game = GameManager.getGame();

        DamageApplier.removeInvincibleEntity(p);
        game.addPlayerLobbyPVPing(p);

        ChampionsPlayer champion = ChampionsPlayerManager.getInstance().getChampionsPlayer(p);
        ChampionsPlayerManager.getInstance().removeChampionsPlayer(champion);
        ChampionsPlayerManager.getInstance().addChampionsPlayer(champion);

        game.updateLobbyInventory(p);
        SoundPlayer.sendSound(p, "random.pop", 1F, 63);
    }

    /**
     * Check if the lobby item use should happen
     * @param event the event that is being checked
     * @return
     */
    private boolean shouldUse(PlayerInteractEvent event) {
        Game game = GameManager.getGame();
        Player player = event.getPlayer();
        if((player.getItemInHand().getType().equals(Material.AIR) ||
                game.getGameState().equals(GameState.STARTED) ||
                (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK &&
                        event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK))) { return false;}
        if(!(player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName())) return false;
        if(game.getGameState().equals(GameState.LOBBY) && game.getTimer().isRunning()) {
            player.sendMessage(String.format("%sInvicta> %sThis function is disabled while the game is starting.", ChatColor.BLUE, ChatColor.GRAY));
            return false;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void lobbyItemUse(PlayerInteractEvent event) {
        Player user = event.getPlayer();
        if(!shouldUse(event)) {
            return;
        }
        String itemName = event.getItem().getItemMeta().getDisplayName();

        if(itemName.contains("Enable Lobby PVP")) enableGameLobbyPVP(user);
        event.setCancelled(true);
    }
}
