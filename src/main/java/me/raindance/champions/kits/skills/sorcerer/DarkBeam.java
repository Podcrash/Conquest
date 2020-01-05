package me.raindance.champions.kits.skills.sorcerer;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.ChargeUp;
import com.podcrash.api.mc.mob.CustomEntityFirework;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.ColorMaker;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

import static com.podcrash.api.mc.world.BlockUtil.*;

@SkillMetadata(id = 1001, skillType = SkillType.Sorcerer, invType = InvType.SHOVEL)
public class DarkBeam extends Instant implements IEnergy, ICooldown, IConstruct {
    private final int MAX_LEVEL = 5;
    private double damage;
    private double range;
    private int energyUsage;
    private FireworkEffect firework;


    public DarkBeam() {
        this.damage = 8;
        this.range = 25;
        this.energyUsage = 60;
    }

    @Override
    public void afterConstruction() {
        this.firework = FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build();
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(onCooldown() || !rightClickCheck(action)) return;
        setLastUsed(System.currentTimeMillis());
        release();
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

        Location endLoc = null;
        World world = getPlayer().getWorld();
        for(int i = 0; i < range; i += 1) {
            if(isPassable(cur.getBlock())  && playerIsHere(cur, getPlayers()) == null) {
                WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(cur.toVector(), EnumWrappers.Particle.SPELL_MOB, new int[]{0,0,0}, 5, 0,0,0);
                world.getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
                cur.add(inc);
            } else {
                endLoc = cur;
                break;
            }
        }
        burst(endLoc);
    }

    private void burst(Location endLoc) {
        if (endLoc == null) return;
        CustomEntityFirework.spawn(endLoc, firework, getPlayers());
        SoundPlayer.sendSound(endLoc, "fireworks.launch", 1F, 63);
        SoundPlayer.sendSound(getPlayer().getLocation(), "fireworks.launch", 1F, 63);
        int dist = 4;
        int distS = dist * dist;
        for (Player p : BlockUtil.getPlayersInArea(endLoc, 4, getPlayers())) {
            if (isAlly(p) && p == getPlayer()) continue;
            double distanceS = p.getLocation().distanceSquared(endLoc);
            double delta = 1D - distanceS / distS;
            DamageApplier.damage(p, getPlayer(), damage * delta, this, false);
        }
    }
}
