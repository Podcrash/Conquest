package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.MathUtil;
import net.jafama.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

@SkillMetadata(skillType = SkillType.Vanguard, invType = InvType.AXE)
public class Intimidation extends Instant implements TimeResource, ICooldown {
    public Intimidation() {
        super();
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.horse.angry", 1.2F, 77);
        run(10, 5);
    }

    @Override
    public String getName() {
        return "Intimidation";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public void task() {
        Location location = getPlayer().getLocation();
        for (Player victim : getPlayers()) {
            if(victim == getPlayer() || isAlly(victim)) continue;
            if(victim.getLocation().distanceSquared(location) <= 64) {
                int slownesss = (int) ((getPlayer().getHealth() - victim.getHealth())/8D);
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 1, slownesss, false, true);
            }
        }
    }

    @Override
    public boolean cancel() {
        //cancel if time elapsed is more than 3 seconds
        return System.currentTimeMillis() - getLastUsed() >= 3L * 1000L;
    }

    @Override
    public void cleanup() {

    }
}
