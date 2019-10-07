package me.raindance.champions.game.map.types;

import com.podcrash.api.mc.game.objects.IObjective;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import org.bukkit.Location;

import java.util.List;

public interface IMap {

    List<Location> getRedSpawn();

    List<Location> getBlueSpawn();

    List<IObjective> getObjectives();

    List<ItemObjective> getItemObjectives();

    List<WinObjective> getWinObjectives();
}
