package me.raindance.champions.kits.skills.hunter;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

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
        return 16;
    }

    @Override
    public String getName() {
        return "Cornered Beast";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!onCooldown()) {
                StatusApplier.getOrNew(getPlayer()).applyStatus(Status.STRENGTH, selfEffect, 1, false);
                _active = true;
                count = 0;
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_GROWL, 1f, 1f);
                this.setLastUsed(System.currentTimeMillis());
                TimeHandler.repeatedTime(1, 0, this);
                getPlayer().sendMessage(getUsedMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void hit(DamageApplyEvent e) {
        if(e.isCancelled()) return;
        if(!_active) return;
        if (getPlayer() == e.getAttacker()) {
            e.addSource(this);
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_BARK, 1f, 1);
        }else if (getPlayer() == e.getVictim()) {
            e.addSource(() -> "Cornered Beast Weakness");
            e.setModified(true);
            e.setDamage(e.getDamage() + 1);
        }
    }

    @Override
    public void task() {
        if (System.currentTimeMillis() - getLastUsed() >= selfEffect1000 || getGame().isRespawning(getPlayer())) _active = false;
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
