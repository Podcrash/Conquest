package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.mc.economy.Currency;
import com.podcrash.api.mc.economy.IEconomyHandler;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.events.game.GameCaptureEvent;
import com.podcrash.api.mc.events.game.GameEndEvent;
import com.podcrash.api.mc.events.game.GamePickUpEvent;
import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.game.objects.IObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Diamond;
import com.podcrash.api.mc.game.objects.objectives.Star;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class DomRewardsListener extends ListenerBase {
    public DomRewardsListener(JavaPlugin plugin) {
        super(plugin);
    }

    private IEconomyHandler handler = Pluginizer.getSpigotPlugin().getEconomyHandler();

    @EventHandler
    public void onKill(DeathApplyEvent event) {
        if(event.getAttacker() instanceof Player &&
                (GameManager.getGame() != null && !GameManager.getGame().getGameState().equals(GameState.LOBBY))) {
            handler.pay((Player) event.getAttacker(), 10);
        }
    }

    @EventHandler
    public void onCapture(GameCaptureEvent event) {
        IObjective objective = event.getObjective();
        if(objective instanceof CapturePoint) {
            Player player = event.getWho();

            if(!((CapturePoint) objective).getColor().equalsIgnoreCase("white") &&
                    (GameManager.getGame() != null && !GameManager.getGame().getGameState().equals(GameState.LOBBY))){
                handler.pay(player, 10);
            }
        }
    }

    @EventHandler
    public void onPickup(GamePickUpEvent event) {
        IObjective objective = event.getItem();
        if((objective instanceof Diamond || objective instanceof Star) &&
                (GameManager.getGame() != null && !GameManager.getGame().getGameState().equals(GameState.LOBBY))) {
            handler.pay(objective.acquiredByPlayer(), 10);
        }
    }

    //TODO handle ties
    @EventHandler(priority = EventPriority.LOW)
    public void onEnd(GameEndEvent event) {
        Game game = event.getGame();

        Set<GTeam> teams = new HashSet<>();
        GTeam highest = null;
        for(GTeam team : GameManager.getGame().getTeams()) {
            if(highest == null) { highest = team;
            } else if(highest.getScore() < team.getScore()) {highest = team;}

            teams.add(team);
        }
        for(Player p : highest.getBukkitPlayers()) {
            handler.pay(p, 70);
            game.addReward(p, 70);
        }
        for(GTeam team : teams) {
            for(Player p : team.getBukkitPlayers()) {
                handler.pay(p, 30);
                game.addReward(p, 30);
            }
        }

    }

}
