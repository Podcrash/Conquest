package me.raindance.champions.kits.skills.GlobalSkills;

import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class BreakFall extends Passive {
    private final double reduction;
    private final int MAX_LEVEL = 3;

    public BreakFall(Player player, int level) {
        super(player, "Break Fall", level, SkillType.Global, InvType.PASSIVEC);
        reduction = 0.5d + 1.5d * level;
        setDesc("Your fall damage is reduced by %%reduction%% damage.");
        addDescArg("reduction", () -> reduction);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler
    public void fall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();
            if (player == getPlayer()) {
                double totalDamage = event.getFinalDamage() - (reduction + ChampionsPlayerManager.getInstance().getChampionsPlayer(player).getFallDamage());
                if (totalDamage < 0) event.setCancelled(true);
                else {
                    event.setDamage(totalDamage);
                }

            }
        }
    }
}
