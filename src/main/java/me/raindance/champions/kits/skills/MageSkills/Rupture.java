package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import jdk.nashorn.internal.ir.annotations.Ignore;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.util.EntityUtil;
import me.raindance.champions.util.TitleSender;
import me.raindance.champions.world.BlockUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;

public class Rupture extends Instant implements TimeResource {
    private final int MAX_LEVEL = 5;
    private Location currentLocation;
    private float power;
    private float rate;
    /*
    1. Right click sword (and be grounded) .
    2. Hold it to manuever
    3. Release: you do stuff
     */

    public Rupture(Player player, int level) {
        super(player, "Rupture", level, SkillType.Mage, ItemType.SWORD, InvType.SWORD, -1);
        this.rate = 0.05f;
        setDesc(Arrays.asList(
                "Hold Block to create a rupture ",
                "at your feet. It will snake through ",
                "the ground, moving towards ",
                "where you are looking. ",
                "",
                "NOTE: This skill is still a work ",
                "in progress!"
        ));
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action) && event.getPlayer() == getPlayer() && EntityUtil.onGround(getPlayer())) {
            currentLocation = BlockUtil.getHighestUnderneath(getPlayer().getLocation());
            TimeHandler.repeatedTime(1, 5, new RuptureMaker(this));
            TimeHandler.repeatedTime(1, 0, this);
        }
    }

    @Ignore
    @Override
    public void task() {
        WrappedChatComponent progress = chargeUp(this.getCharge());
        TitleSender.sendTitle(getPlayer(), progress);
        addCharge();
        final int blockID = currentLocation.getBlock().getTypeId();
        getPlayer().sendMessage(blockID + "");
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(currentLocation.toVector(), EnumWrappers.Particle.BLOCK_CRACK, new int[]{blockID}, 30, 1/8, 1, 1/8);
        //Bukkit.broadcastMessage(String.format("%s: %f %f %f", getEntity().getName(), currentLocation.getX(), currentLocation.getY(), currentLocation.getZ()));
        getPlayer().getWorld().getPlayers().forEach(player -> ParticleGenerator.generate(player, particle));
        getPlayer().getWorld().playSound(currentLocation, Sound.STEP_STONE, 0.9f, 1f);

    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking();
    }

    @Override
    public void cleanup() {
        TitleSender.sendTitle(getPlayer(), TitleSender.emptyTitle());
        resetCharge();
    }

    private void addCharge() {
        power += rate;
        power = (power >= 1f) ? 1f : power;
    }

    private float getCharge() {
        return power;
    }

    private void resetCharge() {
        power = 0;
    }

    private WrappedChatComponent chargeUp(double progress) {
        String bar = TitleSender.generateBars("||");
        int size = bar.length() - 1;
        int currentProgress = (int) (size * progress);
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        String builder = String.format("%s: %s%s %s %s%s %d%%",
                getName(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, (int) (100f * progress));

        return TitleSender.writeTitle(builder);
    }

    private class RuptureMaker implements TimeResource {
        private Rupture rupture;

        public RuptureMaker(Rupture rupture) {
            this.rupture = rupture;
        }

        @Override
        public void task() {
            Vector vector = getPlayer().getLocation().getDirection();
            Location eyeLocation = getPlayer().getTargetBlock((HashSet<Byte>) null, 100).getLocation();
            Location tempLocation;
            if (BlockUtil.isPassable(eyeLocation.getBlock())) { //it is AIR
                tempLocation = currentLocation.clone();
                vector.setY(0);
                tempLocation.add(vector.normalize());
                if (!(tempLocation.getBlock().isEmpty() || tempLocation.getBlock().isLiquid())) {
                    currentLocation = tempLocation;
                }
            } else { //it is NOT AIR
                double x0 = currentLocation.getX();
                double y0 = currentLocation.getY();
                double z0 = currentLocation.getZ();

                double x1 = eyeLocation.getX();
                double y1 = eyeLocation.getY();
                double z1 = eyeLocation.getZ();

                double k = 0.1d;
                double newX = x0 + k * (x1 - x0);
                double newY = y0 + k * (y1 - y0);
                double newZ = z0 + k * (z1 - z0);

                tempLocation = new Location(getPlayer().getWorld(), newX, newY, newZ);
            }
            if (eyeLocation.distanceSquared(currentLocation) <= 0.5) {

            } else {
                if (!tempLocation.getBlock().isLiquid()) {
                /*
                while(tempLocation.getBlock().isEmpty()){
                    tempLocation.add(0, -0.1, 0);
                }
                */
                    currentLocation = tempLocation;

                }
            }
        }

        @Override
        public boolean cancel() {
            return rupture.cancel();
        }

        @Override
        public void cleanup() {
        }
    }
}
