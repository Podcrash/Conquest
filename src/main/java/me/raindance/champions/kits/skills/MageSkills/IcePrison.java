package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.sound.SoundWrapper;
import me.raindance.champions.world.BlockUtil;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class IcePrison extends Instant implements IEnergy {
    private final Random random = new Random();
    private final WrapperPlayServerWorldParticles particles;
    private final String NAME;
    private int energy;
    private int duration;
    public IcePrison(Player player, int level) {
        super(player, "Ice Prison", level, SkillType.Mage, ItemType.AXE, InvType.AXE, 21 - level);
        this.energy = 60 - 3 * level;
        this.duration = 3 + level;
        particles = (player == null) ? null : ParticleGenerator.createParticle(null, EnumWrappers.Particle.SNOW_SHOVEL, 1, 0,0,0);
        NAME = (player == null) ? null : getPlayer().getName()  + getName();
        setDesc(Arrays.asList(
                "Launch an icy orb. When it collides, ",
                "it creates a hollow sphere of ice ",
                "that lasts for %%duration%% seconds. ",
                "",
                "Energy: %%energy%% "
        ));
        addDescArg("duration", () ->  duration);
        addDescArg("energy", () -> energy);
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action)) return;
        if(!onCooldown()) {
            if(!hasEnergy()) {
                getPlayer().sendMessage(getNoEnergyMessage());
                return;
            }
            this.setLastUsed(System.currentTimeMillis());
            Location location = getPlayer().getEyeLocation();
            Vector vector = location.getDirection();
            vector.normalize().multiply(1.15D);
            useEnergy(energy);
            Item item = ItemManipulationManager.intercept(getPlayer(), Material.ICE, location, vector, ((item1, entity) -> {
                Location location1 = item1.getLocation();
                if(entity == null) location1.add(new Vector(0,1,0));
                Set<Vector> blocks = BlockUtil.getOuterBlocksWithinRange(location1, 4, true);
                World world = location1.getWorld();
                for(Vector block : blocks) {
                    BlockUtil.restoreAfterBreak(block.toLocation(world), Material.ICE, (byte) 0, 3 * this.duration/4 + (int) (0.5F + this.duration/4 * random.nextFloat()));
                }
                item1.getWorld().playEffect(location1, Effect.STEP_SOUND, 79);
                item1.remove();
            }));
            ItemMeta meta = item.getItemStack().getItemMeta();
            item.setCustomName("RITB");
            meta.setDisplayName(NAME + item.getEntityId());
            item.getItemStack().setItemMeta(meta);
            ParticleGenerator.generateEntity(item, particles, new SoundWrapper("random.fizz", 0.6F, 88));
            item.getWorld().playSound(item.getLocation(), Sound.SILVERFISH_HIT, 2f, 1f);
        }
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getEnergyUsage() {
        return energy;
    }
}
