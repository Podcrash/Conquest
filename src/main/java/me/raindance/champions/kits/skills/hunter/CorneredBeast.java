package me.raindance.champions.kits.skills.hunter;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
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

@SkillMetadata(id = 401, skillType = SkillType.Hunter, invType = InvType.AXE)
public class CorneredBeast extends Instant implements TimeResource, ICooldown {
    private int count = 0;
    private int selfEffect;
    private boolean _active;
    private long selfEffect1000;
    private final Random rand = new Random();

    public CorneredBeast() {
        this.selfEffect = 5;
        this.selfEffect1000 = selfEffect * 1000L;
    }

    @Override
    public float getCooldown() {
        return 21;
    }

    @Override
    public String getName() {
        return "Cornered Beast";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
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
            e.addSource(this);
            e.setDoKnockback(false);
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_BARK, 1f, 1);
        }
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
