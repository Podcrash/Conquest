package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.mc.listeners.ListenerBase;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DomMapMaintain extends ListenerBase {
    public DomMapMaintain(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleFall(EntityDamageEvent entityDamageEvent) {
        if(entityDamageEvent.isCancelled() || entityDamageEvent.getCause() != EntityDamageEvent.DamageCause.FALL &&
            !(entityDamageEvent.getEntity() instanceof Player)) return;
        ChampionsPlayer championsPlayer = ChampionsPlayerManager.getInstance()
                .getChampionsPlayer((Player) entityDamageEvent.getEntity());
        if(championsPlayer == null) return;
        double after = entityDamageEvent.getDamage() - championsPlayer.getFallDamage();
        if(after <= 0) entityDamageEvent.setCancelled(true);
        else entityDamageEvent.setDamage(after);
    }
}
