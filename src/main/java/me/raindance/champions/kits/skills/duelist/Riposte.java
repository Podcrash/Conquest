package me.raindance.champions.kits.skills.duelist;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

@SkillMetadata(id = 308, skillType = SkillType.Duelist, invType = InvType.SWORD)
public class Riposte extends Instant implements TimeResource, ICooldown {
    private boolean isRiposting = false;
    private boolean ripoSuccess;
    private long ripoSuccessTime;
    private final int MAX_LEVEL = 5;
    private long time;

    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public String getName() {
        return "Riposte";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;
        if (onCooldown()) return;
        time = System.currentTimeMillis();
        isRiposting = true;
        TimeHandler.repeatedTime(1, 0, this);
        getPlayer().sendMessage(getUsedMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        LivingEntity player = event.getVictim();
        if (player == getPlayer() && isRiposting) {
            isRiposting = false;
            event.setCancelled(true);
            TimeHandler.unregister(this);
            LivingEntity victim = event.getAttacker();
            getPlayer().sendMessage(getUsedMessage(victim).replace("used", "replaced"));
            player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.5f, 1.6f);
            ripoSuccess = true;
            ripoSuccessTime = System.currentTimeMillis();
            this.setLastUsed(System.currentTimeMillis());
        }
        if (event.getAttacker() == getPlayer() && ripoSuccess) {
            if (System.currentTimeMillis() - ripoSuccessTime < 1200L) {
                event.setDamage(event.getDamage() + 0.5);
                event.addSource(this);
                player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 1f, 1.6f);
                getPlayer().sendMessage("Skill> You riposted " + event.getVictim().getName());
                event.setModified(true);
            } else getPlayer().sendMessage(getFailedMessage());
            ripoSuccess = false;
        }
    }

    @Override
    public void task() {

    }

    @Override
    public boolean cancel() {
        if (isRiposting) {
            if (!getPlayer().isBlocking()) {
                return true;
            } else if (System.currentTimeMillis() - time >= 999L) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void cleanup() {
        this.setLastUsed(System.currentTimeMillis());
        getPlayer().sendMessage(getFailedMessage());
        TimeHandler.unregister(this);
        isRiposting = false;
    }
}
