package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.Main;
import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.ICharge;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.time.TimeHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Arrays;
import java.util.HashMap;

public class Sharpshooter extends Passive implements ICharge {
    private final HashMap<Integer, Float> forceMap = new HashMap<>();
    private int charges = 0;
    private int miss = 0;
    private final int MAX_CHARGES;
    private long time;
    private boolean justMissed;

    public Sharpshooter(Player player, int level) {
        super(player, "Sharpshooter", level,  SkillType.Ranger, InvType.PASSIVEB);
        MAX_CHARGES = 2 * level;
        setDesc(Arrays.asList(
                "Hitting with arrows increases ",
                "arrow damage by 1 for 5 seconds. ",
                "",
                "Stacks up to %%max%% times, and each ",
                "hit will reset the duration to 5 seconds."
        ));
        addDescArg("max", () ->  MAX_CHARGES);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void shoot(EntityShootBowEvent event){
        if(event.getEntity() == getPlayer() && event.getProjectile() instanceof Arrow) {
            forceMap.put(event.getProjectile().getEntityId(), event.getForce());
        }
    }

    private boolean checkIfValidShooter(DamageApplyEvent e){
        return !e.isCancelled() && e.getVictim() != getPlayer() && e.getAttacker() == getPlayer()
                && e.getArrow() != null && e.getCause() == Cause.PROJECTILE;
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void shoot(DamageApplyEvent e) {
        if (checkIfValidShooter(e)) {
            justMissed = false;
            time = System.currentTimeMillis();
            e.setModified(true);
            e.setDamage(e.getDamage() + getCurrentCharges() * 2);
            int id = e.getArrow().getEntityId();
            if(forceMap.get(id) >= 0.9F) addCharge();
            forceMap.remove(id);
            getPlayer().sendMessage(String.format("%s bonus: %d", getName(), getCurrentCharges()));
            e.addSkillCause(this);
            playSound();
            miss = 0;
            start();
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> justMissed = true, 3L);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void hit(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() == getPlayer()) {
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                if (justMissed) {
                    miss++;
                    if (miss >= 2) {
                        miss = 0;
                        if (charges != 0) {
                            resetCharge();
                            playSound();
                            getPlayer().sendMessage(String.format("%s bonus: %d", getName(), getCurrentCharges()));
                        }
                    }
                }
            }, 1L);
        }
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void addCharge() {
        if (charges < MAX_CHARGES) charges++;
    }

    @Override
    public int getCurrentCharges() {
        return charges;
    }

    @Override
    public int getMaxCharges() {
        return MAX_CHARGES;
    }

    public void resetCharge() {
        charges = 0;
    }

    private void start() {
        stop();
        TimeHandler.repeatedTime(1, 0, this);
    }

    private void stop() {
        TimeHandler.unregister(this);
    }

    @Override
    public void task() {

    }

    @Override
    public boolean cancel() {
        return System.currentTimeMillis() - time >= 5000L;
    }

    @Override
    public void cleanup() {
        resetCharge();
        getPlayer().sendMessage(String.format("%s bonus: %d", getName(), getCurrentCharges()));
        playSound();
    }

    private void playSound() {
        float i = (((float) getCurrentCharges()) / ((float) getMaxCharges()));
        SoundPlayer.sendSound(this.getPlayer(), "note.harp", 0.75f, (int) (130 * i));
    }
}
