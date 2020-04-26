package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.events.DeathApplyEvent;
import com.podcrash.api.events.EnableLobbyPVPEvent;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.listeners.ListenerBase;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import com.podcrash.api.kits.Skill;
import com.podcrash.api.kits.skilltypes.TogglePassive;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public class LobbyListener extends ListenerBase {


    public LobbyListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLobbyDeath(DeathApplyEvent event) {
        Game game = GameManager.getGame();
        Player p = event.getPlayer();

        if (game.getGameState() != GameState.LOBBY) return;

        KitPlayer champion = KitPlayerManager.getInstance().getChampionsPlayer(p);
        champion.resetCooldowns();
        Set<Skill> skills = KitPlayerManager.getInstance().getChampionsPlayer(p).getSkills();
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
    @EventHandler
    public void applyConquestLobbyPVP(EnableLobbyPVPEvent event) {
        if(!event.getGameType().equalsIgnoreCase("Conquest")) { return;}

        Game game = GameManager.getGame();
        Player player = event.getPlayer();

        if(game.getGameState().equals(GameState.LOBBY) && game.getTimer().isRunning()) {
            player.sendMessage(String.format("%sInvicta> %sThis function is disabled while the game is starting.", ChatColor.BLUE, ChatColor.GRAY));
        }

        game.addPlayerLobbyPVPing(player);

        KitPlayer champion = KitPlayerManager.getInstance().getChampionsPlayer(player);
        KitPlayerManager.getInstance().removeKitPlayer(champion);
        KitPlayerManager.getInstance().addKitPlayer(champion);

        game.updateLobbyInventory(player);
    }
}
