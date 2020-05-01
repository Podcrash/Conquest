package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.sound.SoundPlayer;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 903, skillType = SkillType.Warden, invType = InvType.PRIMARY_PASSIVE)
public class Condemn extends Passive implements ICooldown {
    @Override
    public float getCooldown() {
        return 9;
    }

    @Override
    public String getName() {
        return "Condemn";
    }

    @Override
    public ItemType getItemType() {
        return null;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if (onCooldown() || e.getAttacker() != getPlayer()) return;
        if (e.getCause() != Cause.MELEE && e.getCause() != Cause.MELEESKILL) return;
        if (isAlly(e.getVictim())) return;

        setLastUsed(System.currentTimeMillis());
        getPlayer().sendMessage(getUsedMessage(e.getVictim()).replace("used", "unleashed"));
        StatusApplier.getOrNew((Player) e.getVictim()).applyStatus(Status.GROUND, 2F, 1);
        e.addSource(this);

        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.irongolem.hit", 0.7F, 77);
        ParticleGenerator.createBlockEffect(getPlayer().getLocation(), Material.WOODEN_DOOR.getId());
    }
}
