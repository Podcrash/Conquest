package me.raindance.champions.resource;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.events.skill.SkillCooldownEvent;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.TitleSender;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.util.SkillTitleSender;
import org.bukkit.entity.Player;

/**
 * Class used to show cooldowns for skills
 * {@link me.raindance.champions.listeners.maintainers.SkillMaintainListener#toCooldown(SkillCooldownEvent)}
 */
public class CooldownResource implements TimeResource {
    private ICooldown skill;
    private Player player;
    private boolean switcher;
    public CooldownResource(ICooldown skill) {
        this.skill = skill;
        this.player = skill.getPlayer();
    }

    public CooldownResource(SkillCooldownEvent event) {
        this((ICooldown) event.getSkill());
    }

    public ICooldown getSkill() {
        return skill;
    }

    @Override
    public void task() {
        Skill skill = ChampionsPlayerManager.getInstance().getChampionsPlayer(player).getCurrentSkillInHand();
        if(skill != null && skill == this.skill) {
            WrappedChatComponent component = SkillTitleSender.coolDownBar(this.skill);
            TitleSender.sendTitle(player, component);
            if(!switcher) switcher = true;
        } else if (switcher) {
            TitleSender.sendTitle(player, TitleSender.emptyTitle());
            switcher = false;
        }
    }

    @Override
    public boolean cancel() {
        return !skill.onCooldown();
    }

    @Override
    public void cleanup() {
        player.sendMessage(skill.getCanUseMessage());
        TitleSender.sendTitle(player, TitleSender.emptyTitle());
        SoundPlayer.sendSound(player, "note.harp", 0.2f, 160);

    }

    @Override
    public String toString(){
        return String.format("CooldownResource{%s:%s}", player.getName(), skill.getName());
    }
}
