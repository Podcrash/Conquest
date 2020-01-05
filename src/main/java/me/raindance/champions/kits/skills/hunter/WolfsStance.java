package me.raindance.champions.kits.skills.hunter;

import com.podcrash.api.mc.callback.sources.CollideBeforeHitGround;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.skilltypes.ChargeUp;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@SkillMetadata(id = 408, skillType = SkillType.Hunter, invType = InvType.SWORD)
public class WolfsStance extends ChargeUp implements IConstruct {
    private final double damage = 4;
    private final int effectTime = 3;
    private CollideBeforeHitGround hitGround;


    @Override
    public void afterConstruction() {
        this.hitGround = new CollideBeforeHitGround(getPlayer()).then(() -> {
            List<Entity> entities = getPlayer().getNearbyEntities(1.15, 1.15, 1.15);
            if (entities.size() == 0) return;
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity && entity != getPlayer()) {
                    getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_BARK, 0.5f, 1.0f);
                    DamageApplier.damage((LivingEntity) entity, getPlayer(), damage * getCharge(), true);
                    StatusApplier.getOrNew((LivingEntity) entity).applyStatus(Status.SLOW, effectTime, 0);
                    getPlayer().sendMessage(getUsedMessage((LivingEntity) entity));
                }
            }
        });
    }

    @Override
    public float getRate() {
        return (0.24f + 0.08f * 4) / 20f;
    }

    @Override
    public float getCooldown() {
        return 8F;
    }

    @Override
    public String getName() {
        return "Wolf's Stance";
    }

    /*
    @Override
    @EventHandler(priority = EventPriority.HIGH)
    public void block(PlayerInteractEvent e){
        if(((Entity) e.getEntity()).isOnGround()) super.block(e);
    }
    */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(DamageApplyEvent e) {
        if(e.isCancelled()) return;
        if (e.getVictim() == this.getPlayer() && isUsing) {
            resetWhenDamaged();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e) {
        if(e.isCancelled()) return;
        if (e.getEntity() == this.getPlayer() && isUsing) {
            resetWhenDamaged();
        }
    }

    private void resetWhenDamaged() {
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_HURT, 1, 1);
        resetCharge();
    }

    @Override
    public void release() {
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_BARK, 1, (float) (0.5 + 1.5 * getCharge()));
        getPlayer().setFallDistance(-1);
        Vector vector = getPlayer().getLocation().getDirection();
        vector = vector.normalize().multiply(0.5d + 1.35d * getCharge());
        getPlayer().sendMessage(getUsedMessage());
        vector.setY(vector.getY() + 0.2);
        double yMax = 0.5d + 0.82d * getCharge();
        if (vector.getY() > yMax) vector.setY(yMax);
        if (EntityUtil.onGround(getPlayer())) vector.setY(vector.getY() + 0.2);
        getPlayer().setVelocity(vector);
        this.hitGround.run();
    }

    @Override
    public void task() {
        if (EntityUtil.onGround(getPlayer())) {
            super.task();
        }
    }

    @Override
    public void charge() {
        if (EntityUtil.onGround(getPlayer())) {
            super.charge();
        }
    }

    @Override
    public void cleanup() {
        if (getCharge() > 0f) super.cleanup();
    }
}
