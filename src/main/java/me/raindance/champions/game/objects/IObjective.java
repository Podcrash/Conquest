package me.raindance.champions.game.objects;

import me.raindance.champions.Main;
import me.raindance.champions.game.objects.objectives.ObjectiveType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IObjective {

    /**
     * Spawn a firework!
     */
    void spawnFirework();
    Player acquiredByPlayer();
    String acquiredByTeam();
    ObjectiveType getObjectiveType();
    void setAcquiredByPlayer(Player acquirer);
    Location getLocation();
    String getName();

    default void log(String s){
        Main.getInstance().getLogger().info(String.format("[%s{%s}]: %s", this.getClass().getSimpleName(), getName(), s));
    }
}
