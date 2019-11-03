package me.raindance.champions.kits.skills.AssassinSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import static com.podcrash.api.mc.world.BlockUtil.isSafe;
import static com.podcrash.api.mc.world.BlockUtil.playerIsHere;

public class Blink extends Instant {
    private final int MAX_LEVEL = 4;
    private final double distance;
    private Location prevLocation;
    private final int deblinkThreshold; // in seconds

    public Blink(Player player, int level) {
        super(player, "Blink", level, SkillType.Assassin, ItemType.AXE, InvType.AXE, 9 + level);
        distance = 12 + 3 * this.level;
        deblinkThreshold = 6 - this.level;
        setDesc(
                "Right click to instantly teleport forwards %%distance%% blocks.",
                "Right clicking again within %%deblinkThreshold%% seconds",
                "teleports you to your original location.");
        addDescArg("distance", () -> distance);
        addDescArg("deblinkThreshold", () -> deblinkThreshold);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    protected void doSkill(PlayerInteractEvent e, Action action) {
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;
        Player player = getPlayer();
        if (!this.onCooldown()) {
            player.setFallDistance(0);
            blink();
            player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.4f, 1.2f);
            this.setLastUsed(System.currentTimeMillis());
        } else if (onCooldown()
                && this.cooldown - cooldown() < deblinkThreshold
                && prevLocation != null) {
            player.setFallDistance(0);
            deBlink();
            player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.4f, 1.2f);
        } else {
            //this.getEntity().sendMessage(getCooldownMessage());
        }
    }

    public void blink() {
        prevLocation = getPlayer().getLocation();
        Location location = getPlayer().getLocation().add(new Vector(0, 0.4, 0));
        Vector increment = getPlayer().getLocation().getDirection();
        getPlayer().getWorld().playSound(location, Sound.GHAST_FIREBALL, 0.4f, 1.2f);
        for (int i = 0; i < distance; i++) {
            if (!isSafe(location) && playerIsHere(location, getPlayers()) != getPlayer()) {
                location.subtract(increment);
                break;
            }
            else {
                WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(location.clone().add(0, 1, 0).toVector(), EnumWrappers.Particle.SMOKE_LARGE, 5, 0,0,0);
                getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
                location.add(increment);
            }
        }

        getPlayer().teleport(location);
        getPlayer().sendMessage(getUsedMessage());
    }

    public void deBlink() {
        if (prevLocation != null) {
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.GHAST_FIREBALL, 0.4f, 1.2f);

            //this is the location where the player would begin their deblink process
            Location start = getPlayer().getLocation();

            // calculates the final distance (in blocks) required to get back to the pre-blink position
            double x = start.getX() - prevLocation.getX();
            double z = start.getZ() - prevLocation.getZ();
            double finalDistance = FastMath.hypot(x, z);

            Vector increment = prevLocation.toVector().subtract(start.toVector()).normalize();

            for(int i = 0; i < finalDistance; i++) {

                WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(start.clone().add(0, 1, 0).toVector(), EnumWrappers.Particle.SMOKE_LARGE, 5, 0,0,0);
                getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
                start.add(increment);

            }
            getPlayer().teleport(prevLocation);
            prevLocation = null;
            getPlayer().sendMessage(getUsedMessage().replace(getName(), "Deblink"));
        }
    }
}
