package me.raindance.champions.kits.skills.duelist;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.abstractpackets.packetwrapper.WrapperPlayClientUseEntity;
import com.abstractpackets.packetwrapper.WrapperPlayServerEntityStatus;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.SWORD)
public class DeadlyCombination extends Interaction implements ICooldown {
    private LivingEntity attacked = null;
    private int i;
    @Override
    public void doSkill(LivingEntity clickedEntity) {
        this.attacked = clickedEntity;
        this.i = 0;
        setLastUsed(System.currentTimeMillis());
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(attacked.getEntityId());
        packet.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);

        AbstractPacket packet2 = ParticleGenerator.createBlockEffect(attacked.getLocation().toVector(), Material.OBSIDIAN.getId());
        for(Player player : getPlayers()) {
            packet.sendPacket(player);
            packet2.sendPacket(player);
        }
    }

    @EventHandler
    public void hit(DamageApplyEvent event) {
        if(attacked == null || event.getVictim() != attacked || event.getAttacker() != getPlayer()) return;
        if(!(attacked instanceof Player)) return;
        i++;

        if(i < 2 && System.currentTimeMillis() - getLastUsed() > 3000L) return;
        StatusApplier.getOrNew((Player) attacked).applyStatus(Status.SLOW, 2, 2);
        DamageApplier.damage(attacked, getPlayer(), 2, this, false);

        attacked = null;
    }


    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public String getName() {
        return "Deadly Combination";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
