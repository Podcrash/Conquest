package me.raindance.champions.game.map.types;

import me.raindance.champions.game.map.MapManager;
import me.raindance.champions.game.objects.IObjective;
import me.raindance.champions.game.objects.ItemObjective;
import me.raindance.champions.game.objects.WinObjective;
import me.raindance.champions.game.objects.objectives.CapturePoint;
import me.raindance.champions.game.objects.objectives.Emerald;
import me.raindance.champions.game.objects.objectives.Restock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class DominateMap implements IMap {
    private String worldName;

    private List<Location> redSpawn;
    private List<Location> blueSpawn;

    private List<CapturePoint> capturePoints;
    private List<Emerald> emeralds;
    private List<Restock> restocks;

    public DominateMap(World world, String worldName) {
        this.worldName = worldName;
        this.redSpawn = MapManager.getInstance().loadRedSpawns(world, worldName);
        this.blueSpawn = MapManager.getInstance().loadBlueSpawns(world, worldName);
        this.capturePoints = MapManager.getInstance().loadCapturePoints(world, worldName);
        this.emeralds = MapManager.getInstance().loadEmeralds(world, worldName);
        this.restocks = MapManager.getInstance().loadRestocks(world, worldName);
        if(this.redSpawn.size() != 5 || this.blueSpawn.size() != 5 || this.capturePoints.size() == 0 || this.emeralds.size() == 0 || this.restocks.size() == 0){
            throw new RuntimeException("something went wrong with the creation of the dominatemap.");
        }
    }

    public DominateMap(List<Location> redSpawn, List<Location> blueSpawn, List<CapturePoint> capturePoints, List<Emerald> emeralds, List<Restock> restocks) {
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.capturePoints = capturePoints;
        this.emeralds = emeralds;
        this.restocks = restocks;

    }

    public String getInfo(){
        String sep = System.getProperty("line.seperator"); // if we need this
        return String.format(
                "World name: %s \n" +
                "Red spawn: %s \n" +
                "Blue spawn: %s \n" +
                "Capture Points: %s \n" +
                "Emeralds: %s \n +" +
                "Restocks: %s", this.worldName, this.redSpawn.toString(), this.blueSpawn.toString(), this.capturePoints.toString(), this.emeralds.toString(), this.restocks.toString());
    }

    public List<CapturePoint> getCapturePoints() {
        return capturePoints;
    }

    public List<Emerald> getEmeralds() {
        return emeralds;
    }

    public List<Restock> getRestocks() {
        return restocks;
    }

    @Override
    public List<Location> getRedSpawn() {
        return redSpawn;
    }

    @Override
    public List<Location> getBlueSpawn() {
        return blueSpawn;
    }

    public World getBaseWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    @Override
    public List<IObjective> getObjectives() {
        List<IObjective> temporary = new ArrayList<>();
        temporary.addAll(this.getItemObjectives());
        temporary.addAll(this.getWinObjectives());
        return temporary;
    }

    @Override
    public List<ItemObjective> getItemObjectives() {
        List<ItemObjective> temporary = new ArrayList<>();
        temporary.addAll(getEmeralds());
        temporary.addAll(getRestocks());
        return temporary;
    }

    @Override
    public List<WinObjective> getWinObjectives() {
        List<WinObjective> winObjectives = new ArrayList<>();
        winObjectives.addAll(getCapturePoints());
        return winObjectives;
    }
}
