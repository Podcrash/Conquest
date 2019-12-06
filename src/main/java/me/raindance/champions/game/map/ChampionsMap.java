package me.raindance.champions.game.map;

import com.podcrash.api.mc.game.objects.IObjective;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public interface ChampionsMap {
    List<Vector> getRedSpawn();
    List<Vector> getBlueSpawn();

    List<CapturePoint> getCapturePoints();
    List<Emerald> getEmeralds();
    List<Restock> getRestocks();

    List<IObjective> getObjectives();
    List<ItemObjective> getItemObjectives();
    List<WinObjective> getWinObjectives();

}
