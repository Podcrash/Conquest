package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.events.SoundApplyEvent;
import com.podcrash.api.listeners.ListenerBase;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
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
        ChampionsPlayer championsPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer((Player) event.getVictim());
        event.setSound(championsPlayer.getSound());
    }
}
