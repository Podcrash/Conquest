package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.callback.sources.CollideBeforeHitGround;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.util.EntityUtil;
import net.jafama.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class SeismicSlam extends Instant implements TimeResource {
    private final int MAX_LEVEL = 5;
    private boolean usage = false;
    private double reach;
    private int damage;

    public SeismicSlam(Player player, int level) {
        super(player, "Seismic Slam", level, SkillType.Brute, ItemType.AXE, InvType.AXE, 21 - level);
        this.reach = FastMath.pow((5.5d + this.level * 0.5d), 2d);
        this.damage = 1 + this.level;
        setDesc(Arrays.asList(
                "Jump up and slam back into the ground. ",
                "Players within %%range%% blocks take up to ",
                "%%damage%% damage and are smashed away from you. "
        ));
        addDescArg("range", () ->  Math.sqrt(reach));
        addDescArg("damage", () -> damage);
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!this.onCooldown() && !usage) {
                usage = true;
                Vector vector = getPlayer().getLocation().getDirection();
                if(vector.getY() < 0) vector.setY(vector.getY() * -1);
                vector.normalize();
                vector.multiply(0.6);
                vector.setY(vector.getY() + 0.8d);
                if(vector.getY() > 0.8D) vector.setY(0.8);
                if(EntityUtil.onGround(getPlayer())) vector.setY(vector.getY() + 0.2);
                getPlayer().setVelocity(vector);
                setLastUsed(System.currentTimeMillis());
                getPlayer().sendMessage(getUsedMessage());
                getPlayer().setFallDistance(-2f);
                this.runAsync(1, 0);
            }
        }
    }

    @Override
    public void task() {

    }

    @Override
    public boolean cancel() {
        return !usage || (System.currentTimeMillis() - getLastUsed() >= 200L && EntityUtil.onGround(getPlayer()));
    }

    @Override
    public void cleanup() {
        if (!usage) return;
        usage = false;
        SkillUseEvent event = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        ParticleGenerator.generateRangeParticles(getPlayer().getLocation(), FastMath.sqrt(this.reach), true);
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_WOOD, 2f, 0.2f);
        List<Player> players = getPlayer().getWorld().getPlayers();
        for (Player possibleVictim : players) {
            if (possibleVictim != getPlayer() && possibleVictim.getLocation().distanceSquared(getPlayer().getLocation()) <= this.reach) {

                Vector vector = possibleVictim.getLocation().subtract(getPlayer().getLocation()).toVector();

                vector.normalize().multiply(1.7d).setY(0.9);
                DamageApplier.damage(possibleVictim, getPlayer(), this.damage, this, false);
                possibleVictim.setVelocity(vector);
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
}
