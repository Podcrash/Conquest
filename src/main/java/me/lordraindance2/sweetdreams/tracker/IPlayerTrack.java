package me.lordraindance2.sweetdreams.tracker;


import org.bukkit.entity.Player;

public interface IPlayerTrack<T> extends Tracker {
    T get(Player player);
}
