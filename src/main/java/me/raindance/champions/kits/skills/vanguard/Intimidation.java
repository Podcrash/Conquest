package me.raindance.champions.kits.skills.vanguard;

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
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

@SkillMetadata(id = 805, skillType = SkillType.Vanguard, invType = InvType.AXE)
public class Intimidation extends Instant implements TimeResource, ICooldown {
    public Intimidation() {
        super();
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.horse.angry", 1.2F, 77);
        run(10, 5);
    }

    @Override
    public String getName() {
        return "Intimidation";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public void task() {
        Location location = getPlayer().getLocation();
        WrapperPlayServerWorldParticles particles = ParticleGenerator.createParticle(EnumWrappers.Particle.PORTAL, 2);
        List<Player> players = getPlayers();
        PacketUtil.asyncSend(particles, players);
        for (Player victim : players) {
            if(victim == getPlayer() || isAlly(victim)) continue;
            if(victim.getLocation().distanceSquared(location) > 64) continue;
            int slowLevel = (int) ((getPlayer().getHealth() - victim.getHealth())/8D);
            StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 1, slowLevel, false, true);

        }
    }

    @Override
    public boolean cancel() {
        //cancel if time elapsed is more than 3 seconds
        return System.currentTimeMillis() - getLastUsed() >= 3L * 1000L;
    }

    @Override
    public void cleanup() {

    }
}
