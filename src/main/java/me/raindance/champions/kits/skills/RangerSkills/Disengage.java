package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Disengage extends Instant implements TimeResource {
    private final int MAX_LEVEL = 4;
    private boolean isDisengaging;
    private boolean tempFallCancel;
    private long time;
    private float effectTime;

    public Disengage(Player player, int level) {
        super(player, "Disengage", level, SkillType.Ranger, ItemType.SWORD, InvType.SWORD, 16 - level);
        effectTime = 2.5f + 0.5f * level;
        setDesc(Arrays.asList(
                "Block an attack within 1 second after ",
                "blocking to disengage. ",
                "",
                "If successful, you leap backwards ",
                "and your attacker receives Slow 4 ",
                "for %%duration%% seconds."
        ));
        addDescArg("duration", () ->  effectTime);
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

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!onCooldown()) {
                isDisengaging = true;
                time = System.currentTimeMillis();
                TimeHandler.repeatedTime(1, 0, this);
                getPlayer().sendMessage(String.format("Skill> You are trying to %s", getName()));
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void hit(DamageApplyEvent event) {
        if (isDisengaging && event.getVictim() == getPlayer() && event.getCause() == Cause.MELEE) {
            if(!(event.getAttacker() instanceof Player)) return;
            Player victim = (Player) event.getAttacker();
            event.setCancelled(true);
            isDisengaging = false;
            tempFallCancel = true;
            StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, effectTime, 3);
            Vector vector = victim.getLocation().getDirection().normalize();
            vector.multiply(0.5d + 1.35d * 0.95).setY(0.9);
            getPlayer().setVelocity(vector);
            getPlayer().sendMessage(getUsedMessage());
            setLastUsed(System.currentTimeMillis());


        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void fall(EntityDamageEvent event) {
        if (tempFallCancel && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() == getPlayer()) {
                event.setCancelled(true);
                tempFallCancel = false;
            }
        }
    }

    @Override
    public void task() {
    }

    @Override
    public boolean cancel() {
        return (System.currentTimeMillis() - time >= 999L || !getPlayer().isBlocking());
    }

    @Override
    public void cleanup() {
        if (isDisengaging) {
            getPlayer().sendMessage("Skill> You failed Disengage");
            setLastUsed(System.currentTimeMillis());
        }
        isDisengaging = false;
    }
}
