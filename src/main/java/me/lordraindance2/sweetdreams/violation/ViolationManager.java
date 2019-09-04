package me.lordraindance2.sweetdreams.violation;

import me.lordraindance2.sweetdreams.checks.CheckType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class ViolationManager {
    private static List<Violation> violations = new ArrayList<>();

    public static void addViolation(Player player, ViolationType violationType, CheckType checkType) {
        violations.add(new Violation(player, violationType, checkType));
    }

    public List<Violation> getViolations(Player player) {
        List<Violation> indiVio = new ArrayList<>();
        for(Violation violation : violations) {
            if(violation.getPlayerName().equals(player.getName()))
                indiVio.add(violation);
        }
        return indiVio;
    }
    public static void clearViolation(Player player) {
        clearViolation(player.getName());
    }
    public static void clearViolation(String name) {
        violations.removeIf(vio -> vio.getPlayerName().equals(name));
    }
}
