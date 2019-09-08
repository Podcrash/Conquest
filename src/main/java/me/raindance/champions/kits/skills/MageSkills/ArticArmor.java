package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import me.raindance.champions.time.resources.BlockBreakThenRestore;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.util.PacketUtil;
import me.raindance.champions.world.BlockUtil;
import me.raindance.champions.world.CraftBlockUpdater;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ArticArmor extends TogglePassive implements IEnergy, TimeResource {
    private int energeUsage;
    private int radius;
    private double radiusSquared;
    private final Random random = new Random();
    public ArticArmor(Player player, int level) {
        super(player, "Artic Armor", level, "snow", SkillType.Mage, InvType.PASSIVEA);
        this.energeUsage = (11 - level)/2;
        this.radius = 3 + level;
        this.radiusSquared = FastMath.pow(radius, 2);
        setDesc(Arrays.asList(
                "Drop Axe/Sword to Toggle. ",
                "",
                "Create a freezing area around you ",
                "in a %%radius%% block radius. Allies inside ",
                "this area receive Protection 2. ",
                "",
                "You receive Protection 2. ",
                "",
                "Energy: %%energy%% per Second"
        ));
        addDescArg("radius", () ->  radius);
        addDescArg("energy", () -> energeUsage);
    }

    @Override
    public int getEnergyUsage() {
        return energeUsage;
    }


    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void toggle() {
        if(isToggled()) {
            if(hasEnergy(energeUsage)) {
                useEnergy(energeUsage);
                run(1, 0);
            }
        } else {
            getChampionsPlayer().getEnergyBar().toggleRegen(true);
            unregister();
        }
    }

    private void protect() {
        Location location = getPlayer().getLocation();
        location.getWorld().playSound(location, Sound.AMBIENCE_RAIN, 0.3f, 0f);
        Iterable<Block> waterBlocks = BlockUtil.getSpecificWithinRange(location, radius, Material.WATER, Material.STATIONARY_WATER, Material.ICE);

        List<BlockBreakThenRestore> restores = CraftBlockUpdater.getMassBlockUpdater(location.getWorld()).getRestores();
        for (Block block : waterBlocks) {
            if(block.getY() < location.getBlockY()) {
                if(block.getType() != Material.ICE) {
                    BlockUtil.restoreAfterBreak(block.getLocation(), Material.ICE, (byte) 0, 4);
                } else {
                    for(BlockBreakThenRestore restore : restores) {
                        Location restoreLoc = restore.getLocation();
                        if(block.getX() == restoreLoc.getBlockX() && block.getZ() == restoreLoc.getZ()) restore.setDuration(4);
                    }
                }
            }

        }
        Set<Vector> vectors = BlockUtil.getOuterBlocksWithinRange(location, radius, false);
        double currentY = location.getY();
        double minus = currentY - 1;
        for (Vector vector : vectors) {
            Location up = vector.toLocation(location.getWorld());
            Block current = up.getBlock();
            if(current.getRelative(BlockFace.UP).getType() == Material.AIR && current.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                if(minus < current.getY() && current.getY() <= currentY){
                    WrapperPlayServerWorldParticles snow = ParticleGenerator.createParticle(up.toVector(), EnumWrappers.Particle.SNOW_SHOVEL, 1,
                            random.nextFloat() * 0.5F, 0.3F, random.nextFloat() * 0.5F);
                    PacketUtil.syncSend(snow, getPlayers());
                }
            }
        }

        for(Player player : getPlayers()) {
            StatusApplier applier = StatusApplier.getOrNew(player);
            if(player == getPlayer()) applier.applyStatus(Status.RESISTANCE, 0.5F, 0, true, true);
            else if(isAlly(player) && player.getLocation().distanceSquared(getPlayer().getLocation()) <= this.radiusSquared) {
                applier.applyStatus(Status.RESISTANCE, 0.5F, 1, true);
            }
        }
    }

    @Override
    public void task() {
        if (hasEnergy()) {
            protect();
            useEnergy(getEnergyUsageTicks());
            getChampionsPlayer().getEnergyBar().toggleRegen(false);
        } else {
            this.getPlayer().sendMessage(getNoEnergyMessage());
            if(isToggled()) forceToggle();
            getChampionsPlayer().getEnergyBar().toggleRegen(true);
        }
    }

    @Override
    public boolean cancel() {
        return !isToggled();
    }

    @Override
    public void cleanup() {

    }
}
