package me.raindance.champions.kits.skills.GlobalSkills;

import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class Swim extends Passive {
    private final int MAX_LEVEL = 3;
    private double velocityBonus = 0.5;

    public Swim(Player player, int level) {
        super(player, "Swim", level, SkillType.Global, InvType.PASSIVEC, 2F - 0.5F * level);
        velocityBonus += level * 0.08;
        setDesc("Pressing shift will launch you while in the water.",
                "Higher levels will launch you further");
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onSneak(PlayerToggleSneakEvent event) {
        if(onCooldown()) return;
        if (event.getPlayer() == getPlayer() && isInWater() && !getPlayer().isSneaking()) {
            Vector velocity = getPlayer().getLocation().getDirection().normalize();
            Vector applied = velocity.multiply(velocityBonus);
            getPlayer().sendMessage(getUsedMessage());
            getPlayer().setVelocity(applied.setY(applied.getY() * 0.01));
            setLastUsed(System.currentTimeMillis());
        }
    }
}
