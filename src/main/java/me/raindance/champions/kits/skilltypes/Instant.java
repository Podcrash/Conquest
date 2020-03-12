package me.raindance.champions.kits.skilltypes;

import com.podcrash.api.mc.time.TimeHandler;
import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class Instant extends Skill {
    private final Skill instance;
    private boolean canUseWhileCooldown;
    private boolean use;
    public Instant() {
        super();
        instance = this;
        canUseWhileCooldown = false;
    }

    @EventHandler( priority = EventPriority.HIGHEST )
    public void interact(PlayerInteractEvent e) {
        if (!canUseSkill(e)) return;
        Main.getInstance().log.info("Regular Interact Called");
        if(skill(e, e.getAction())) {
            Main.getInstance().log.info("Regular Interact Passed");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractAtEntityEvent e) {
        if (!canUseSkill(e)) return;
        Main.getInstance().log.info("Interact At Called");
        if(skill(e, Action.RIGHT_CLICK_AIR)) {
            Main.getInstance().log.info("Regular Interact At Passed");
        }

    }

    public boolean canUseSkill(PlayerEvent event) {
        if (getPlayer() != event.getPlayer() || !isHolding()) return false;
        if(!canUseWhileCooldown && this instanceof ICooldown)
            if((((ICooldown) this).onCooldown())) return false;

        if(!(this instanceof BowShotSkill) && event instanceof PlayerInteractEvent) {
            if(!rightClickCheck(((PlayerInteractEvent) event).getAction()))
                return false;
        }
        if (isInWater()) {
            getPlayer().sendMessage(getWaterMessage());
            return false;
        } else return true;
    }

    protected void setCanUseWhileCooldown(boolean canUseWhileCooldown) {
        this.canUseWhileCooldown = canUseWhileCooldown;
    }
    private boolean skill(PlayerEvent event, Action action) {
        if(this.use) return false;
        SkillUseEvent useEvent = new SkillUseEvent(instance);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return false;
        this.use = true;
        doSkill(event, action);
        TimeHandler.delayTime(1L, () -> use = false);
        return true;
    }
    protected abstract void doSkill(PlayerEvent event, Action action);
}