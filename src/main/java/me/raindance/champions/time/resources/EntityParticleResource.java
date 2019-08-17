package me.raindance.champions.time.resources;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.sound.SoundWrapper;
import org.bukkit.entity.Entity;

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
        entity.getWorld().getPlayers().forEach(player -> packet.sendPacket(player));
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