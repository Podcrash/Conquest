package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.listeners.ListenerBase;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.classes.Assassin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ArrowNerf extends ListenerBase {
    public ArrowNerf(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void damage(DamageApplyEvent event) {
        if(event.getAttacker() instanceof Player  &&
                ChampionsPlayerManager.getInstance().getChampionsPlayer((Player) event.getAttacker()) instanceof Assassin) {
            event.setDamage(event.getDamage() * .625D);
        }
    }
}
