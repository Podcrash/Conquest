package me.raindance.champions.kits.skills.RangerSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.Random;

public class WolfsFury extends Instant implements TimeResource {
    private final int MAX_LEVEL = 4;
    private int count = 0;
    private int selfEffect;
    private float selfReduction;
    private boolean _active;
    private long selfEffect1000;
    private final Random rand = new Random();

    public WolfsFury(Player player, int level) {
        super(player, "Wolf's Fury", level, SkillType.Ranger, ItemType.AXE, InvType.AXE, 14 + level);
        this.selfEffect = 3 + level;
        this.selfReduction = 0.45f + 0.05f * level;
        this.selfEffect1000 = selfEffect * 1000L;
        setDesc(Arrays.asList(
                "Summon the power of the wolf, gaining ",
                "Strength 4 for %%duration%% seconds, and giving ",
                "no knockback on your attacks.",
                "",
                "If you miss two consecutive attacks, ",
                "Wolfs Fury ends."
        ));
        addDescArg("duration", () ->  selfEffect);
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void leftclickBlock(PlayerInteractEvent e) {
        if (e.getPlayer() == getPlayer() && _active &&
                (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
            count++;
            if (count >= 2) {
                _active = false;

            }
        }
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!onCooldown()) {
                StatusApplier.getOrNew(getPlayer()).applyStatus(Status.STRENGTH, selfEffect, 3, false);
                _active = true;
                count = 0;
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_GROWL, 1f, 1f);
                this.setLastUsed(System.currentTimeMillis());
                TimeHandler.repeatedTime(1, 0, this);
                getPlayer().sendMessage(getUsedMessage());
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void hit(DamageApplyEvent e) {
        if(e.isCancelled()) return;
        if (_active && getPlayer() == e.getAttacker()) {
            e.setModified(true);
            e.addSkillCause(this);
            e.setDoKnockback(false);
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_BARK, 1f, 1);
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public void task() {
        if (System.currentTimeMillis() - getLastUsed() >= selfEffect1000) _active = false;
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(getPlayer().getLocation().toVector(),
                EnumWrappers.Particle.REDSTONE, new int[]{255, 255, 255, 0}, 9,
                rand.nextFloat() / 2f, 0.25f + (rand.nextFloat() - 0.15f), rand.nextFloat() / 2f);
        getPlayer().getWorld().getPlayers().forEach(player -> ParticleGenerator.generate(player, particle));
        //TODO: PARTICLE GENERATOR
    }

    @Override
    public boolean cancel() {
        return !_active;
    }

    @Override
    public void cleanup() {
        _active = false;
        StatusApplier.getOrNew(getPlayer()).removeVanilla(Status.STRENGTH);
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_WHINE, 1, 1);
    }
}
