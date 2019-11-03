package me.raindance.champions.kits.skills.AssassinSkills;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.VectorUtil;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class Backstab extends Passive {
    private final int MAX_LEVEL = 3;
    private float bonus;

    public Backstab(Player player, int level) {
        super(player, "Backstab", level, SkillType.Assassin, InvType.PASSIVEB);
        bonus = 1f + level * 1f;

        this.setDesc(
                "Attacks from behind opponents deal",
                "extra %%bonus%% damage.");
        this.addDescArg("bonus", () -> bonus);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onHit(DamageApplyEvent event) {
        //cba with non players
        if (event.isCancelled() || event.getCause() != Cause.MELEE) return;
        if (event.getAttacker() != getPlayer()) return;
        Player damager = (Player) event.getAttacker();
        LivingEntity victim = event.getVictim();
        if (VectorUtil.angleIsAround(damager.getLocation().getDirection(), victim.getLocation().getDirection(), 60)) {
            event.setModified(true);
            event.addSource(this);
            event.setDamage(event.getDamage() + bonus);
            SoundPlayer.sendSound(victim.getLocation(), "game.neutral.hurt", 0.5F, 126);
            victim.getWorld().playEffect(event.getVictim().getLocation(), Effect.STEP_SOUND, 55);
        }
    }
}
