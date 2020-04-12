package me.raindance.champions.kits.skills.sorcerer;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import com.podcrash.api.mc.mob.CustomEntityFirework;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

import java.util.List;

import static com.podcrash.api.mc.world.BlockUtil.*;

@SkillMetadata(id = 1001, skillType = SkillType.Sorcerer, invType = InvType.SHOVEL)
public class DarkBeam extends Instant implements IEnergy, ICooldown, IConstruct {
    private final int MAX_LEVEL = 5;
    private double damage = 7;
    private double range = 25;
    private int energyUsage = 40;
    private FireworkEffect firework;

    private double detectionRadius = 1.5;
    private int damageRadius = 2;

    public DarkBeam() {}

    @Override
    public void afterConstruction() {
        this.firework = FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build();

    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(onCooldown() || !rightClickCheck(action)) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }
        useEnergy();
        setLastUsed(System.currentTimeMillis());
        release();

        getPlayer().sendMessage(getUsedMessage());
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    @Override
    public float getCooldown() {
        return 5;
    }

    @Override
    public String getName() {
        return "Dark Beam";
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    public void release(){
        Location cur = getPlayer().getEyeLocation();
        Vector inc = cur.getDirection().normalize();
        cur.add(inc);
        List<Player> players = getPlayers();
        for(int i = 0; i < range; i += 1) {
            //if the block wasn't passible, stop
            if(!isPassable(cur.getBlock())) break;

            //if a player is within the point within a sphere, then break
            if(hasPlayersInArea(cur, detectionRadius, players, getPlayer()))
                break;
            WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(cur.toVector(), EnumWrappers.Particle.SPELL_MOB, new int[]{0,0,0}, 5, 0,0,0);
            PacketUtil.asyncSend(packet, players);
            cur.add(inc);
        }
        burst(cur, players);
    }
    /*
    private boolean hasPlayersInArea(Location location, double radius, List<Player> players) {
        double radiusSquared = radius * radius;
        for(Player player : players) {
            if(player == getPlayer() && isAlly(player)) continue;
            Location loc = player.getLocation();
            double distanceSquared = loc.distanceSquared(location);
            if(distanceSquared <= radiusSquared)
                return true;
        }
        return false;
    }

     */
    private void burst(Location endLoc, List<Player> players) {
        if (endLoc == null) return;
        CustomEntityFirework.spawn(endLoc, firework, players);
        SoundPlayer.sendSound(endLoc, "fireworks.launch", 1F, 63);
        SoundPlayer.sendSound(getPlayer().getLocation(), "fireworks.launch", 1F, 63);
        for (Player p : BlockUtil.getPlayersInArea(endLoc, damageRadius, players)) {
            if (isAlly(p) || p == getPlayer()) continue;
            DamageApplier.damage(p, getPlayer(), damage, this, true);
            return;
        }
    }

    @EventHandler
    public void damage(DamageApplyEvent event) {
        if(!event.containsSource(this)) return; //this time only run when it contains it...
        event.setVelocityModifierX(event.getVelocityModifierX() * 0.6);
        event.setVelocityModifierY(event.getVelocityModifierY() * 0.6);
        event.setVelocityModifierZ(event.getVelocityModifierZ() * 0.6);
    }
}
