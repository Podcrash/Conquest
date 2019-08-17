package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.callback.sources.CollideBeforeHitGround;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.ChargeUp;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class WolfsPounce extends ChargeUp {
    private final int MAX_LEVEL = 4;
    private double damage;
    private final int effectTime = 3;
    private CollideBeforeHitGround hitGround;

    public WolfsPounce(Player player, int level) {
        super(player, "Wolf's Pounce", level, SkillType.Ranger, ItemType.SWORD, InvType.SWORD, 8 - level, 0);
        this.rate = (0.24f + 0.08f * level) / 20f;
        int wholeNumberRate = (int) ((rate * 20) * 100);
        this.damage =  level;
        this.hitGround = (CollideBeforeHitGround) new CollideBeforeHitGround(getPlayer()).then(() -> {
            List<Entity> entities = getPlayer().getNearbyEntities(1.15, 1.15, 1.15);
            if (entities.size() == 0) return;
            for (Entity entity : entities) {
                if (entity instanceof Player && entity != getPlayer()) {
                    getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.WOLF_BARK, 0.5f, 1.0f);
                    DamageApplier.damage((Player) entity, getPlayer(), damage * getCharge(), true);
                    StatusApplier.getOrNew((Player) entity).applyStatus(Status.SLOW, effectTime, 0);
                    getPlayer().sendMessage(String.format("You used Wolf's Pounce on %s", entity.getName()));
                }
            }
        });
        setDesc(Arrays.asList(
                "Hold block to charge pounce. ",
                "Release Block to pounce. ",
                "",
                "Charges %%rate%%% per Second. ",
                "Taking damage cancels charge. ",
                "",
                "Colliding with another player ",
                "mid-air deals up to %%damage%% damage ",
                "and Slow 2 for 3 seconds. "
        ));
        addDescArg("rate", () ->  wholeNumberRate);
        addDescArg("damage", () -> damage);
    }

    /*
    @Override
    @EventHandler(priority = EventPriority.HIGH)
    public void block(PlayerInteractEvent e){
        if(((Entity) e.getEntity()).isOnGround()) super.block(e);
    }
    */
    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(DamageApplyEvent e) {
        if (e.getVictim() == this.getPlayer() && isUsing) {
            resetWhenDamaged();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e) {
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
        if (((Entity) getPlayer()).isOnGround()) vector.setY(vector.getY() + 0.2);
        getPlayer().setVelocity(vector);
        this.hitGround.run();
    }

    @Override
    public void task() {
        if (((Entity) getPlayer()).isOnGround()) {
            super.task();
        }
    }

    @Override
    public void charge() {
        if (((Entity) getPlayer()).isOnGround()) {
            super.charge();
        }
    }

    @Override
    public void cleanup() {
        if (getCharge() > 0f) super.cleanup();
    }
}
