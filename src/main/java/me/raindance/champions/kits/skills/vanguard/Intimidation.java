package me.raindance.champions.kits.skills.vanguard;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.PacketUtil;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

@SkillMetadata(id = 805, skillType = SkillType.Vanguard, invType = InvType.SHOVEL)
public class Intimidation extends Instant implements ICooldown {
    public Intimidation() {
        super();
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.horse.angry", 1.2F, 77);

        Location location = getPlayer().getLocation();
        WrapperPlayServerWorldParticles particles = ParticleGenerator.createParticle(EnumWrappers.Particle.PORTAL, 2);
        List<Player> players = getPlayers();
        PacketUtil.asyncSend(particles, players);
        for (Player victim : players) {
            if(victim == getPlayer() || isAlly(victim)) continue;
            if(victim.getLocation().distanceSquared(location) > 64) continue;
            int slowLevel = 1;
            StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 5, slowLevel, false, true);

        }

        getPlayer().sendMessage(getUsedMessage());
    }

    @Override
    public String getName() {
        return "Intimidation";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    @Override
    public float getCooldown() {
        return 14;
    }
}
