package me.raindance.champions.game.map.types;

import me.raindance.champions.game.objects.IObjective;
import me.raindance.champions.game.objects.ItemObjective;
import me.raindance.champions.game.objects.WinObjective;
import org.bukkit.Location;

import java.util.List;

public interface IMap {

    List<Location> getRedSpawn();

    List<Location> getBlueSpawn();

    List<IObjective> getObjectives();

    List<ItemObjective> getItemObjectives();

    List<WinObjective> getWinObjectives();
}
