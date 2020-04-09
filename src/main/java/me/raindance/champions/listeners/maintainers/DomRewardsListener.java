package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.mc.economy.IEconomyHandler;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.events.game.GameEndEvent;
import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DomRewardsListener extends ListenerBase {
    public DomRewardsListener(JavaPlugin plugin) {
        super(plugin);
    }

    private IEconomyHandler handler = Pluginizer.getSpigotPlugin().getEconomyHandler();

    @EventHandler
    public void onKill(DeathApplyEvent event) {
        if(event.getAttacker() instanceof Player) {
            handler.pay((Player) event.getAttacker(), 10);
        }

    }

    //TODO handle ties
    @EventHandler
    public void onEnd(GameEndEvent event) {
        Set<GTeam> teams = new HashSet<>();
        GTeam highest = null;
        for(GTeam team : GameManager.getGame().getTeams()) {
            if(highest == null) { highest = team;
            } else if(highest.getScore() < team.getScore()) {highest = team;}

            teams.add(team);
        }
        for(Player p : highest.getBukkitPlayers()) {
            handler.pay(p, 70);
        }
        for(GTeam team : teams) {
            for(Player p : team.getBukkitPlayers()) {
                handler.pay(p, 30);
            }
        }
    }

}
