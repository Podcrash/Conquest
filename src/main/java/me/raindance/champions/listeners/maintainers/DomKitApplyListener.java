package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.events.skill.ApplyKitEvent;
import com.podcrash.api.listeners.ListenerBase;
import me.raindance.champions.kits.ChampionsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class DomKitApplyListener extends ListenerBase {
    public DomKitApplyListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void apply(ApplyKitEvent e) {
        ChampionsPlayer cPlayer = (ChampionsPlayer) e.getKitPlayer();
        cPlayer.getPlayer().sendMessage(cPlayer.skillsRead());
    }
}
