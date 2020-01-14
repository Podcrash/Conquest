package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.events.DeathApplyEvent;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.EntityUtil;
import net.jafama.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

import java.util.List;

@SkillMetadata(id = 107, skillType = SkillType.Berserker, invType = InvType.AXE)
public class SeismicShockwave extends Instant implements TimeResource, ICooldown {
    private boolean usage = false;
    private final double reach = Math.pow((5.5d), 2d);
    private final int damage = 4;

    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public String getName() {
        return "Seismic Shockwave";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!this.onCooldown() && !usage) {
                usage = true;
                Vector vector = getPlayer().getLocation().getDirection();
                if(vector.getY() < 0) vector.setY(vector.getY() * -1);
                vector.normalize();
                vector.multiply(0.3);
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

    @EventHandler
    public void die(DeathApplyEvent event) {
        if(event.getPlayer() == getPlayer()) usage = false;
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
}
