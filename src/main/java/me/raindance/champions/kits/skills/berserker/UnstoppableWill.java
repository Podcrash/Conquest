package me.raindance.champions.kits.skills.berserker;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@SkillMetadata(skillType = SkillType.Berserker, invType = InvType.AXE)
public class UnstoppableWill extends Instant implements ICooldown {

    @Override
    public float getCooldown() {
        return 17;
    }

    @Override
    public String getName() {
        return "Unstoppable Will";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action) && onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        applier.getEffects().forEach(status -> {
            if(status.isNegative())
                applier.removeStatus(status);
        });
        applier.applyStatus(Status.RESISTANCE, 4, 1);
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.irongolem.hit", 0.8F, 70);
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(EnumWrappers.Particle.VILLAGER_ANGRY, 4);
        packet.setLocation(getPlayer().getEyeLocation());
        PacketUtil.asyncSend(packet, getPlayers());
    }
}
