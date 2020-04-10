package me.raindance.champions.kits.skills.warden;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.annotation.SkillInit;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

@SkillMetadata(id = 902, skillType = SkillType.Warden, invType = InvType.AXE)
public class ConcussiveBlow extends Instant implements ICooldown {
    private float velocity;

    @Override
    public float getCooldown() {
        return 12;
    }

    @Override
    public String getName() {
        return "Concussive Blow";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @SkillInit
    public void init() {
        velocity = 4 * 0.25F + 1.9F;
    }

    //TODO: MATH
    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(action != Action.RIGHT_CLICK_BLOCK  && action != Action.RIGHT_CLICK_AIR) return;
        if(onCooldown()) return;

        Location eyeLoc = getPlayer().getEyeLocation();
        Location center = eyeLoc.add(eyeLoc.getDirection().clone().normalize().multiply(1.2d));
        AbstractPacket packet = ParticleGenerator.createParticle(center.toVector(), EnumWrappers.Particle.EXPLOSION_LARGE,
                1, 0, 0 , 0);
        Vector vector = eyeLoc.getDirection().normalize().multiply(velocity);
        if(vector.getY() > 0.75) vector.setY(0.75);

        boolean makeSound = false;
        for(Player p : getPlayers()) {
            ParticleGenerator.generate(p, packet);
            if(p == getPlayer() || p.getLocation().distanceSquared(center) > 10D) continue;
            makeSound = true;
            p.setVelocity(vector);
        }

        if(makeSound) SoundPlayer.sendSound(getPlayer().getLocation(), "mob.zombie.metal", 1.1F, 57);
        this.setLastUsed(System.currentTimeMillis());

        getPlayer().sendMessage(getUsedMessage());
    }
}
