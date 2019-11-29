package me.raindance.champions.kits.skills.KnightSkills;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FatalStrike extends Passive {
    public FatalStrike(Player player, int level) {
        super(player, "Fatal Strike", 1, SkillType.Knight, InvType.PASSIVEB);
    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(e.getAttacker() != getPlayer() && !(e.getVictim() instanceof Player)) return;
        if(e.getVictim().getHealth() > e.getVictim().getMaxHealth()/2D) return;
        StatusApplier.getOrNew((Player) e.getVictim()).applyStatus(Status.BLEED, 3, 1);
        AbstractPacket packet = ParticleGenerator.createBlockEffect(e.getVictim().getLocation(), Material.REDSTONE.getId());
        PacketUtil.asyncSend(packet, getPlayers());
    }
    @Override
    public int getMaxLevel() {
        return 1;
    }
}
