package me.raindance.champions.kits.skills.duelist;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.PASSIVEB)
public class FatalStrike extends Passive {

    @Override
    public String getName() {
        return "Fatal Strike";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(e.getAttacker() != getPlayer() && !(e.getVictim() instanceof Player)) return;
        if(e.getVictim().getHealth() > e.getVictim().getMaxHealth()/2D) return;
        StatusApplier.getOrNew((Player) e.getVictim()).applyStatus(Status.BLEED, 3, 1);
        AbstractPacket packet = ParticleGenerator.createBlockEffect(e.getVictim().getLocation(), Material.REDSTONE.getId());
        PacketUtil.asyncSend(packet, getPlayers());
    }
}
