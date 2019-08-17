package me.raindance.champions.kits.iskilltypes;

import org.bukkit.entity.Player;

/**
 * This class will be used for making changes after the championsPlayer is made (as skills are made before them)
 * Good example: mana pool
 */
public interface IConstruct {
    Player getPlayer();
    default void doConstruct() {
        if(getPlayer() != null) afterConstruction();
    }
    void afterConstruction();
}
