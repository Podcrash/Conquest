package me.raindance.champions.kits.skills.KnightSkills;

import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class Riposte extends Instant implements TimeResource {
    private boolean isRiposting = false;
    private boolean ripoSuccess;
    private long ripoSuccessTime;
    private final int MAX_LEVEL = 5;
    private final double bonus;
    private long time;

    public Riposte(Player player, int level) {
        super(player, "Riposte", level, SkillType.Knight, ItemType.SWORD, InvType.SWORD, 15 - level);
        bonus = 0.5d * level;
        setDesc(Arrays.asList(
                "Block an incoming attack to parry, ",
                "then quickly return the attack ",
                "to riposte.",
                "",
                "If successful, you deal an additional ",
                "%%damage%% bonus damage.",
                "",
                "You must block, parry, then riposte ",
                "all within 1 second of each other."
        ));
        addDescArg("damage", () ->  bonus);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!onCooldown()) {
            time = System.currentTimeMillis();
            isRiposting = true;
            TimeHandler.repeatedTime(1, 0, this);
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        LivingEntity player = event.getVictim();
        if (player == getPlayer() && isRiposting) {
            isRiposting = false;
            event.setCancelled(true);
            TimeHandler.unregister(this);
            LivingEntity victim = event.getAttacker();
            player.sendMessage("Skill> You parried " + victim.getName());
            player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.5f, 1.6f);
            ripoSuccess = true;
            ripoSuccessTime = System.currentTimeMillis();
            this.setLastUsed(System.currentTimeMillis());
        }
        if (event.getAttacker() == getPlayer() && ripoSuccess) {
            if (System.currentTimeMillis() - ripoSuccessTime < 1200L) {
                event.setDamage(event.getDamage() + bonus);
                event.addSkillCause(this);
                player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 1f, 1.6f);
                getPlayer().sendMessage("Skill> You riposted " + event.getVictim().getName());
                event.setModified(true);
            } else getPlayer().sendMessage("Skill> You failed to Riposte");
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
        getPlayer().sendMessage("You failed riposte");
        TimeHandler.unregister(this);
        isRiposting = false;
    }
}
