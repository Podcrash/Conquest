package me.raindance.champions.kits.skills.druid;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldEvent;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.PacketUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

@SkillMetadata(id = 201, skillType = SkillType.Druid, invType = InvType.AXE)
public class Fissure extends Instant implements IEnergy, TimeResource, ICooldown {
    private int energyUsage = 50;
    private final Vector up = new Vector(0, 1, 0);
    private float damage;
    public Fissure() {
        super();
        this.damage = 7F;
    }

    @Override
    public float getCooldown() {
        return 11;
    }

    @Override
    public String getName() {
        return "Earth Wall";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action)) return;
        if(onCooldown()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }
        if(!(EntityUtil.onGround(getPlayer()))) {
            getPlayer().sendMessage(getMustAirborneMessage());
            return;
        }

        getPlayer().sendMessage(getUsedMessage());
        useEnergy();
        setLastUsed(System.currentTimeMillis());
        Location playerLocation = getPlayer().getLocation();
        dir = playerLocation.getDirection().setY(0).normalize();
        start = playerLocation.add(new Vector(dir.getX(), -0.4, dir.getZ()));
        current = start.clone();
        run(2, 0);
        Location sstart = start.clone();
        end = sstart.clone().add(dir.clone().multiply(7));
        Vector startVector = start.toVector();
        while(BlockUtil.get2dDistanceSquared(startVector, sstart.add(dir).toVector()) <= 49) {
            if(BlockUtil.isPassable(sstart.getBlock())) {
                sstart.subtract(up);
                if(BlockUtil.isPassable(sstart.getBlock())) break;
            }
            WrapperPlayServerWorldEvent packet = ParticleGenerator.createBlockEffect(sstart, sstart.getBlock().getType().getId());
            PacketUtil.asyncSend(packet, getPlayers());

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
                DamageApplier.damage(player, getPlayer(), (i/7D) * damage, this, true);
                StatusApplier.getOrNew(player).applyStatus(Status.SLOW, 3, 0);
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
        if(i < 0) return;

        AbstractPacket packet = ParticleGenerator.createBlockEffect(location, Material.DIRT.getId());
        PacketUtil.syncSend(packet, getPlayers());
        final Location toMake = location.add(up);
        if(!BlockUtil.isPassable(toMake.getBlock())) return;
        BlockUtil.restoreAfterBreak(toMake.clone(), Material.DIRT, new MaterialData(Material.DIRT).getData(), 5);
        erupt(toMake, i - 1);

    }
}
