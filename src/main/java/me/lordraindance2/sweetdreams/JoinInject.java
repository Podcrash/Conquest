package me.lordraindance2.sweetdreams;

import com.google.common.reflect.ClassPath;
import me.lordraindance2.sweetdreams.checks.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.*;

public class JoinInject implements Listener {
    private static List<Player> onlinePlayers = new ArrayList<>();
    private static Map<String, List<Check>> checks = new HashMap<>();
    @EventHandler
    public void join(PlayerJoinEvent e) {
        inject(e.getPlayer());
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        deinject(event.getPlayer());
    }



    public void inject(Player player) {
        onlinePlayers.add(player);
        //addCheck(new ClientSpeedUp(player));
        //addCheck(new NoSlowDown(player));
        //addCheck(new General(player));
        addCheck(new Reach(player));
        //addCheck(new Misplace(player));
        //addCheck(new Clicks(player));
    }
    private void deinject(Player player) {
        onlinePlayers.remove(player);
        if(checks.get(player.getName()) == null) return;
        for(Check check : checks.get(player.getName()))
            check.deinject();
        checks.put(player.getName(), null);
    }

    private void addCheck(Check check) {
        List<Check> playerChecks = checks.getOrDefault(check.getPlayer().getName(), null);
        if(playerChecks == null) playerChecks = new ArrayList<>();
        playerChecks.add(check);
        check.inject();
        checks.put(check.getPlayer().getName(), playerChecks);
    }

    public static List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public static List<Check> getOnlineChecks(CheckType checkType) {
        List<Check> checksWithType = new ArrayList<>();
        for(List<Check> a : checks.values())
            for(Check b : a)
                if(b.getCheckType() == checkType)
                    checksWithType.add(b);
        return checksWithType;
    }
}
