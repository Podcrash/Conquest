package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SkillMetadata(skillType = SkillType.Warden, invType = InvType.PASSIVEA)
public class Condemn extends Passive implements ICooldown {
    @Override
    public float getCooldown() {
        return 18;
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
        if(onCooldown() || e.getAttacker() != getPlayer()) return;
        if(!(e.getVictim() instanceof Player)) return;
        setLastUsed(System.currentTimeMillis());
        getPlayer().sendMessage(getUsedMessage(e.getVictim()).replace("used", "unleashed"));
        StatusApplier.getOrNew((Player) e.getVictim()).applyStatus(Status.GROUND, 1.5F, 1);
        e.setDamage(e.getDamage() - 2);
        e.setModified(true);
        e.addSource(this);
        ParticleGenerator.createBlockEffect(getPlayer().getLocation(), Material.WOODEN_DOOR.getId());
    }
}
