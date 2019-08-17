package me.raindance.champions.time.resources;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.raindance.champions.events.skill.SkillCooldownEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.util.TitleSender;
import org.bukkit.entity.Player;

/**
 * Class used to show cooldowns for skills
 * {@link me.raindance.champions.listeners.maintainers.SkillMaintainListener#toCooldown(SkillCooldownEvent)}
 */
public class CooldownResource implements TimeResource {
    private Skill skill;
    private Player player;
    private boolean switcher;
    private final String lowerName;
    public CooldownResource(Skill skill) {
        this.skill = skill;
        this.lowerName = skill.getItype().getName().toLowerCase();
        this.player = skill.getPlayer();
    }

    public CooldownResource(SkillCooldownEvent event) {
        this(event.getSkill());
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public void task() {
        if (player.getItemInHand().getType().name().toLowerCase().contains(lowerName)) {
            WrappedChatComponent component = TitleSender.coolDownBar(skill);
            TitleSender.sendTitle(player, component);
            if(!switcher) switcher = true;
        } else if (switcher) {
            TitleSender.sendTitle(player, TitleSender.emptyTitle());
            switcher = false;
        }
    }

    @Override
    public boolean cancel() {
        return !skill.isValid() || !skill.onCooldown();
    }

    @Override
    public void cleanup() {
        if(skill.isValid() && skill.getCanUseMessage() != null) {
            player.sendMessage(skill.getCanUseMessage());
            TitleSender.sendTitle(player, TitleSender.emptyTitle());
            SoundPlayer.sendSound(player, "note.harp", 0.2f, 160);
        }
    }

    @Override
    public String toString(){
        return String.format("CooldownResource{%s:%s}", player.getName(), skill.getName());
    }
}
