package me.raindance.champions.kits.skills.KnightSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.Random;

public class HoldPosition extends Instant implements TimeResource {
    private final int MAX_LEVEL = 5;
    private boolean toggled = false;
    private int duration;
    private final int duration1000;
    private final Random rand;
    private boolean active = false;

    public HoldPosition(Player player, int level) {
        super(player, "Hold Position", level, SkillType.Knight, ItemType.AXE, InvType.AXE, 16 + 2 * level);
        duration = 3 + level;
        this.duration1000 = duration * 1000;
        rand = new Random();
        setDesc(Arrays.asList(
                "Hold your position, gaining ",
                "Protection 3, Slow 3 and no ",
                "knockback for %%duration%% seconds. "
        ));
        addDescArg("duration", () ->  duration);
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action) && isHolding()) {
            if (!onCooldown()) {
                toggled = true;
                StatusApplier applier = StatusApplier.getOrNew(getPlayer());
                applier.applyStatus(Status.WEAKNESS, duration, 0);
                applier.applyStatus(Status.RESISTANCE, duration, 2);
                applier.applyStatus(Status.SLOW, duration, 2);
                applier.applyStatus(Status.ROOTED, duration, 1);
                TimeHandler.repeatedTime(1, 0, this);
                setLastUsed(System.currentTimeMillis());
                getPlayer().sendMessage(getUsedMessage());
                SoundPlayer.sendSound(getPlayer().getLocation(), "mob.endermen.scream", 0.75F, 10, getPlayers());
                active = true;
                TimeHandler.repeatedTime(1,0, this);
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public void task() {
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(getPlayer().getLocation().toVector(),
                EnumWrappers.Particle.SPELL_MOB, new int[]{1, 1, 1, 0}, 5,
                rand.nextFloat() / 2f, 0.25f + (rand.nextFloat() - 0.15f), rand.nextFloat() / 2f);
        getPlayer().getWorld().getPlayers().forEach(player -> ParticleGenerator.generate(player, particle));
    }

    @Override
    public boolean cancel() {
        return (System.currentTimeMillis() - getLastUsed() >= duration1000);
    }

    @Override
    public void cleanup() {
        active = false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void damage(DamageApplyEvent e){
        if(e.isCancelled()) return;
        if(active && e.getVictim() == getPlayer()) {
            e.setModified(true);
            e.setDoKnockback(false);
        }
    }
}
