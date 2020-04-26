package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.events.SoundApplyEvent;
import com.podcrash.api.listeners.ListenerBase;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SoundDamage extends ListenerBase {
    public SoundDamage(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void sound(SoundApplyEvent event) {
        if(!(event.getVictim() instanceof Player)) return;
        KitPlayer kitPlayer = KitPlayerManager.getInstance().getChampionsPlayer((Player) event.getVictim());
        event.setSound(kitPlayer.getSound());
    }
}
