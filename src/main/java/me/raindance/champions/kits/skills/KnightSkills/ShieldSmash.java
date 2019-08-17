package me.raindance.champions.kits.skills.KnightSkills;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class ShieldSmash extends Instant {
    private float velocity;
    public ShieldSmash(Player player, int level) {
        super(player, "Shield Smash", level, SkillType.Knight, ItemType.AXE, InvType.AXE, 15 - level);
        velocity = level * 0.2F + 1.6F;
        setDesc(Arrays.asList(
                "Smash your shield into an enemy, ",
                "dealing %%velocity%% knockback. "
        ));
        addDescArg("velocity", () ->  (double)((int)(velocity * 100.0))/100.0);
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(action != Action.RIGHT_CLICK_BLOCK  && action != Action.RIGHT_CLICK_AIR) return;
        if(onCooldown()) {
            return;
        }
        Location eyeLoc = getPlayer().getEyeLocation();
        Location center = eyeLoc.add(eyeLoc.getDirection().clone().normalize().multiply(0.5d));
        AbstractPacket packet = ParticleGenerator.createParticle(center, EnumWrappers.Particle.EXPLOSION_LARGE, 1, 0, 0 , 0);
        for(Player p : getPlayers()) {

            ParticleGenerator.generate(p, packet);
            if(p != getPlayer() && p.getLocation().distanceSquared(center) <= 4D){
                p.setVelocity(eyeLoc.getDirection().normalize().multiply(velocity));
                SoundPlayer.sendSound(p, "mob.zombie.metal", 0.8F, 57);
            }
        }
        this.setLastUsed(System.currentTimeMillis());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
