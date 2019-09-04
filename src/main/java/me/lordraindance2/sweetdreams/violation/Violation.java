package me.lordraindance2.sweetdreams.violation;

import me.lordraindance2.sweetdreams.checks.CheckType;
import me.lordraindance2.sweetdreams.LunarDance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Violation {
    private String player;
    private ViolationType violationType;
    private CheckType checkType;

    public Violation(Player player, ViolationType violationType, CheckType checkType) {
        this.player = player.getName();
        this.violationType = violationType;
        this.checkType = checkType;

        LunarDance.plugin.getLogger().info(String.format("[%s]: %s flagged for %s",
                violationType, player.getName(), checkType));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }
    public String getPlayerName() {
        return player;
    }

    public ViolationType getViolationType() {
        return violationType;
    }

    public CheckType getCheckType() {
        return checkType;
    }
}
