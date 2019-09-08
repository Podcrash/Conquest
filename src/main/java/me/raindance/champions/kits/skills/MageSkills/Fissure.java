package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerWorldEvent;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.util.EntityUtil;
import me.raindance.champions.util.PacketUtil;
import me.raindance.champions.world.BlockUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class Fissure extends Instant implements IEnergy, TimeResource {
    private final Vector up = new Vector(0, 1, 0);
    private final Random rand = new Random();
    private int energy;
    private float duration;
    private float baseDmg;
    private float scaling;
    public Fissure(Player player, int level) {
        super(player, "Fissure", level, SkillType.Mage, ItemType.AXE, InvType.AXE, 13 - level);
        this.energy = 60 - 3 * level;
        this.duration = 2.0F + 0.5F * level;
        this.baseDmg = 1.0F + 0.3F * level;
        this.scaling = 0.5F + 0.15F * level;
        setDesc(Arrays.asList(
                "Fissures the earth in front of you, ",
                "creating an impassable wall. ",
                "",
                "Players struck by the initial slam ",
                "receive Slow 2 for %%duration%% seconds. ",
                "",
                "Players struck by the fissure ",
                "receive %%damage%% damage plus an ",
                "additional %%scaling%% damage for ",
                "every block fissure has travelled.",
                "",
                "Energy: %%energy%%"
        ));
        addDescArg("duration", () ->  duration);
        addDescArg("damage", () -> (double)((int)(baseDmg * 100.0))/100.0);
        addDescArg("scaling", () -> (double)((int)(scaling * 100.0))/100.0);
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
        if(!rightClickCheck(action)) return;
        if(onCooldown()) {
            //getEntity().sendMessage(getCooldownMessage());
        }else if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
        }else {
            if(!(EntityUtil.onGround(getPlayer()))) {
                getPlayer().sendMessage(String.format("%sSkill> %sYou must be grounded to use Fissure.", ChatColor.BLUE, ChatColor.GRAY));
                return;
            }
        useEnergy(energy);
        setLastUsed(System.currentTimeMillis());
        Location playerLocation = getPlayer().getLocation();
        dir = playerLocation.getDirection().setY(0).normalize();
        start = playerLocation.subtract(new Vector(0, 0.4, 0));
        current = start.clone();
        run(3, 0);
        Location sstart = start.clone();
        end = sstart.clone().add(dir.clone().multiply(14));
        Vector startVector = start.toVector();
        while(BlockUtil.get2dDistanceSquared(startVector, sstart.add(dir).toVector()) <= 196) {
            if(BlockUtil.isPassable(sstart.getBlock())) {
                sstart.subtract(up);
                if(BlockUtil.isPassable(sstart.getBlock())) break;
            }
            WrapperPlayServerWorldEvent packet = ParticleGenerator.createBlockEffect(sstart, sstart.getBlock().getType().getId());
            PacketUtil.syncSend(packet, getPlayers());
            for(Player player : getPlayers()) {
                if(player != getPlayer() && !isAlly(player) && player.getLocation().distanceSquared(sstart) <= 1.3225D) {
                    StatusApplier.getOrNew(player).applyStatus(Status.SLOW,  duration, 0);
                }
            }
        }
        }
    }

    private Location current, start, end;
    private Vector dir;
    private boolean cancel = false;
    private int i = 0;
    @Override
    public void task() {
        boolean flag1 = BlockUtil.isPassable(current.getBlock());
        if(flag1) {
            Location down = current.subtract(up);
            boolean flag2 = BlockUtil.isPassable(down.getBlock());
            if(flag2) {
                cancel = true;
                return;
            }
        }else {
            current.add(up);
            if(!BlockUtil.isPassable(current.getBlock().getRelative(BlockFace.UP))) {
                cancel = true;
                return;
            }
            else {
                task();
                return; // run once
            }
        }

        Location an = current.clone();
        Location ref = an.clone();
        if(i <= 1){
            erupt(an, 0);
        }else if(i <= 3){
            erupt(an, 1);
        }else {
            erupt(an, 2);
        }
        i++;
        for(Player player : getPlayers()) {
            if (player != getPlayer() && !isAlly(player) && player.getLocation().distanceSquared(ref) <= 1.3225D) {
                DamageApplier.damage(player, getPlayer(), baseDmg + i * scaling, this, true);
                StatusApplier.getOrNew(player).applyStatus(Status.SLOW, duration, findPotency(i), false, true);
            }
        }
        current.add(dir);
    }

    @Override
    public boolean cancel() {
        return cancel || BlockUtil.get2dDistanceSquared(start.toVector(), current.toVector()) >= 196;
    }

    @Override
    public void cleanup() {
        cancel = false;
        i = 0;
    }

    private void erupt(Location location, final int i){
        if(i < 0) {
            return;
        }
        final Material material = location.getBlock().getType();
        final MaterialData data = location.getBlock().getState().getData();
        AbstractPacket packet = ParticleGenerator.createBlockEffect(location, material.getId());
        PacketUtil.syncSend(packet, getPlayers());
        final Location toMake = location.add(up);
        if(!BlockUtil.isPassable(toMake.getBlock())) return;
        BlockUtil.restoreAfterBreak(toMake.clone(), material, data.getData(), 14 - level);
        erupt(toMake, i - 1);

    }

    private int findPotency(int level) {
        return (int) (level / 3.5d);
    }
}
