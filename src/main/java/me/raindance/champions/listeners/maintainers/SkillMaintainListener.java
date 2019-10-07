package me.raindance.champions.listeners.maintainers;

import me.raindance.champions.events.skill.SkillCooldownEvent;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.listeners.ListenerBase;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.time.TimeHandler;
import me.raindance.champions.resource.CooldownResource;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillMaintainListener extends ListenerBase {
    public SkillMaintainListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void toCooldown(SkillCooldownEvent event){
        if(event.getSkill().hasCooldown() && !((event.getSkill() instanceof Passive) )){
            TimeHandler.repeatedTime(1, 0, new CooldownResource(event));
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void useSkill(SkillUseEvent e){
        if(StatusApplier.getOrNew(e.getPlayer()).isInept()) {
            e.setCancelled(true);
            return;
        }
        ChampionsPlayer championsPlayer = e.getSkill().getChampionsPlayer();
        if(championsPlayer.isSilenced()){
            e.setCancelled(true);
            e.getPlayer().sendMessage(String.format("%sCondition> %sYou are silenced for %s%.2f %sseconds",
                    ChatColor.BLUE,
                    ChatColor.GRAY,
                    ChatColor.GREEN,
                    StatusApplier.getOrNew(e.getPlayer()).getRemainingDuration(Status.SILENCE) / 1000L,
                    ChatColor.GRAY));
        }
    }
}
