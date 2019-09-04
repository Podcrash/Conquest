package me.lordraindance2.sweetdreams;

import com.comphenix.protocol.events.ListenerPriority;
import me.lordraindance2.sweetdreams.checks.Check;
import me.lordraindance2.sweetdreams.checks.CheckType;
import me.lordraindance2.sweetdreams.checks.Reach;
import me.lordraindance2.sweetdreams.util.RayTracer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collection;

public class Commands implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void command(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        String[] args = msg.split(" ");
        if(!event.getPlayer().hasPermission("Champions.developer")) return;
        if(args[0].equalsIgnoreCase("/debug")) {
            if(args.length == 2) {
                boolean cancel = false;
                if (changeLogSetting(args[1])) {
                    cancel = true;
                    event.getPlayer().sendMessage("LunarDance> Logging for " + args[1] + " is now changed.");
                }else event.getPlayer().sendMessage("Something went wrong?");
                event.setCancelled(cancel);
            }
        }else if(args[0].equalsIgnoreCase("/reachacc")) {
            if(args.length == 2) {
                try {
                    double oldAcc = Reach.accuracy;
                    double acc = Double.parseDouble(args[1]);
                    event.getPlayer().sendMessage("[Reach] Accuracy changed from " + oldAcc + " to " + acc + "!");
                    Reach.accuracy = acc;
                    event.setCancelled(true);
                }catch (NumberFormatException e) {
                    event.getPlayer().sendMessage("[Reach] " + args[1] + " is not a proper decimal!");
                }
            }
        }else if(args[0].equalsIgnoreCase("/reachtest")) {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            if(players.size() > 1) {
                int i = 0;
                for(Player player : players) {
                    Location location1 = new Location(player.getWorld(), 59.6, 100,0.004, -90, 0);
                    Location location2 = new Location(player.getWorld(), 63, 100,0, 90, 0);
                    if(i % 2 == 0) {
                        player.teleport(location1);
                    }else player.teleport(location2);
                    i++;
                }
            }
            event.setCancelled(true);
        }else if(args[0].equalsIgnoreCase("/raytracer")) {
            if(args.length == 2) {
                try {
                    double oldAcc = RayTracer.DEFAULT_ESTIMATE;
                    double acc = Double.parseDouble(args[1]);
                    event.getPlayer().sendMessage("[RayTracer] Accuracy changed from " + oldAcc + " to " + acc + "!");
                    RayTracer.DEFAULT_ESTIMATE = acc;
                    event.setCancelled(true);
                }catch (NumberFormatException e) {
                    event.getPlayer().sendMessage("[RayTracer] " + args[1] + " is not a proper decimal!");
                }
            }
            event.setCancelled(true);
        }
    }

    private boolean changeLogSetting(String string) {
        CheckType checkType = CheckType.getByName(string);
        if(checkType == CheckType.NULL) return false;

        for(Check check : JoinInject.getOnlineChecks(checkType)) {
            check.setDisabled(!check.isDisabled());
        }
        return true;
    }
}
