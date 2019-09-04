package me.raindance.champions.time.resources;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.sound.SoundWrapper;
import me.raindance.champions.util.PacketUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Give players particles as used in {@link WrapperPlayServerWorldParticles}
 */
public class EntityParticleResource implements TimeResource {
    protected Entity entity;
    private WrapperPlayServerWorldParticles packet;
    private SoundWrapper sound;
    public boolean cancel = false;

    public EntityParticleResource(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound) {
        this.entity = entity;
        this.packet = packet;
        this.sound = sound;
    }

    @Override
    public void task() {
        packet.setLocation(entity.getLocation());
        for(Player player : entity.getWorld().getPlayers()) {
            if(this.entity instanceof Player) {
                if(!player.canSee((Player) this.entity)) continue;
            }
            packet.sendPacket(player);
        }
        if(sound != null) SoundPlayer.sendSound(entity.getLocation(), sound.getSoundName(), sound.getVolume(), sound.getPitch());
    }

    @Override
    public boolean cancel() {
        return cancel || !entity.isValid();
    }

    @Override
    public void cleanup() {

    }
}