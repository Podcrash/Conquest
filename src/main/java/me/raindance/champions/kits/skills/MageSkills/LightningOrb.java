package me.raindance.champions.kits.skills.MageSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundWrapper;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class LightningOrb extends Instant implements IEnergy {
    private final WrapperPlayServerWorldParticles particles;
    private final String NAME;
    private int energy;
    private float distance;
    private int damage;
    public LightningOrb(Player player, int level) {
        super(player, "Lightning Orb", level, SkillType.Mage, ItemType.AXE, InvType.AXE, 13 - level);
        this.energy = 60 - 2 * level;
        this.distance = 3.0F + 0.5F * level;
        this.distance *= distance;
        this.damage = 4 + level;
        this.particles = (player == null) ? null : ParticleGenerator.createParticle(null, EnumWrappers.Particle.SNOW_SHOVEL, 1, 0, 0, 0);
        this.NAME = (player == null) ? null : getPlayer().getName()  + getName();
        setDesc(Arrays.asList(
                "Launch a lightning orb. Upon a direct ",
                "hit with player, or 1.7 seconds, it will ",
                "strike all enemies withing %%range%% Blocks ",
                "with lightning, dealing %%damage%% damage and ",
                "giving Slow 2 for 4 seconds. ",
                "",
                "Energy: %%energy%%"
        ));
        addDescArg("range", () -> FastMath.sqrt(distance));
        addDescArg("damage", () -> damage);
        addDescArg("energy", () -> energy);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getEnergyUsage() {
        return energy;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        if(isInWater()) getPlayer().sendMessage(getNoEnergyMessage());
        else {
            this.setLastUsed(System.currentTimeMillis());
            Location location = getPlayer().getEyeLocation();
            Vector vector = location.getDirection();
            vector.normalize().multiply(1.15D);
            useEnergy(energy);
            Item item = ItemManipulationManager.interceptWithCooldown(getPlayer(), Material.DIAMOND_BLOCK, location, vector, 2.5F, ((item1, entity) -> {
                Location location1 = item1.getLocation();
                for(Player player : getPlayers()) {
                    if(player == getPlayer() && isAlly(player)) continue;
                    Location playerLocation = player.getLocation();
                    if(location1.distanceSquared(playerLocation) <= distance) {
                        playerLocation.getWorld().strikeLightningEffect(playerLocation);
                        StatusApplier.getOrNew(player).applyStatus(Status.SLOW, 4, 1);
                        StatusApplier.getOrNew(player).applyStatus(Status.SHOCK, 4, 1);
                        DamageApplier.damage(player, getPlayer(), damage, this, false);
                    }
                    item1.remove();
                }
            }));
            item.setCustomName("RITB");
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(NAME + item.getEntityId());
            item.getItemStack().setItemMeta(meta);
            ParticleGenerator.generateEntity(item, particles, new SoundWrapper("random.fizz", 0.6F, 88));
            item.getWorld().playSound(item.getLocation(), Sound.SILVERFISH_HIT, 2f, 1f);
        }
    }

}
