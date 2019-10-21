package me.raindance.champions.kits.skills.KnightSkills;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.VectorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class DefensiveStance extends Skill {
    private final int MAX_LEVEL = 1;
    private final int SKILL_TOKEN_WEIGHT = 2;

    public DefensiveStance(Player player, int level) {
        super(player, "Defensive Stance", level, SkillType.Knight, ItemType.SWORD, InvType.SWORD, -1);
        setDesc(Arrays.asList(
                "While blocking, you take 80% less ",
                "damage from attacks in front of you. "
        ));
    }

    @Override
    public int getSkillTokenWeight() {
        return SKILL_TOKEN_WEIGHT;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void hit(DamageApplyEvent event) {
        if ((event.getCause() == Cause.MELEE || event.getCause() == Cause.PROJECTILE)){
            if(event.getAttacker() == getPlayer()) {
                if(System.currentTimeMillis() - getLastUsed() <= 800L) {
                    event.setCancelled(true);
                }
            }
            if ((event.getVictim() == getPlayer() && getPlayer().isBlocking())) {
                Vector damagerYaw = event.getAttacker().getLocation().getDirection();
                Vector victimYaw = event.getVictim().getLocation().getDirection();
                /*
                Bukkit.broadcastMessage(String.format("damagerYaw: %f victimYaw: %f", damagerYaw, victimYaw));
                Bukkit.broadcastMessage(String.format("R: damagerYaw: %f victimYaw: %f", Math.toDegrees(damagerYaw), Math.toRadians(victimYaw)));
                */
                if (!VectorUtil.angleIsAround(damagerYaw, victimYaw, 135)) {
                    SoundPlayer.sendSound(event.getVictim().getLocation(), "mob.zombie.metal", 0.75F, 126);
                    event.setModified(true);
                    event.setDamage(event.getDamage() * (1D / 3D));
                    event.setDoKnockback(false);
                    this.setLastUsed(System.currentTimeMillis());
                }
            }
        }


    }
}
