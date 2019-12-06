package me.raindance.champions.game.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.podcrash.api.mc.map.BaseGameMap;
import com.podcrash.api.mc.game.objects.IObjective;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import com.podcrash.api.mc.map.JsonHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.*;

public class DominateMap extends BaseGameMap implements ChampionsMap {
    private String worldName;

    private List<CapturePoint> capturePoints;
    private List<Emerald> emeralds;
    private List<Restock> restocks;

    public DominateMap(JsonObject json) {
        super(json);
    }

    public String getInfo(){
        String sep = System.getProperty("line.seperator"); // if we need this
        return String.format(
                "World name: %s \n" +
                "Red spawn: %s \n" +
                "Blue spawn: %s \n" +
                "Capture Points: %s \n" +
                "Emeralds: %s \n +" +
                "Restocks: %s", this.getName(), getRedSpawn().toString(), getBlueSpawn().toString(), this.capturePoints.toString(), this.emeralds.toString(), this.restocks.toString());
    }

    @Override
    public void setGameWorld(World w) {
        super.setGameWorld(w);
        Deque<IObjective> objectives = new ArrayDeque<>();
        objectives.addAll(capturePoints);
        objectives.addAll(emeralds);
        objectives.addAll(restocks);

        for(IObjective objective : objectives)
            objective.setWorld(w);
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);

        capturePoints = new ArrayList<>();
        emeralds = new ArrayList<>();
        restocks = new ArrayList<>();

        JsonObject points = json.getAsJsonObject("capturePoints");
        if(points != null) {
            for (Map.Entry<String, JsonElement> entry : points.entrySet()) {
                double[] vectorArray = JsonHelper.getArray((JsonArray) entry.getValue());
                Vector v = new Vector(vectorArray[0], vectorArray[1], vectorArray[2]);
                CapturePoint capturePoint = new CapturePoint(entry.getKey(), v);
                capturePoints.add(capturePoint);
            }
        }

        JsonArray emeraldsJson = json.getAsJsonArray("emeralds");
        if(emeraldsJson != null) {
            for (int i = 0, size = emeraldsJson.size(); i < size; i++) {
                double[] vectorArray = JsonHelper.getArray(emeraldsJson.get(i).getAsJsonArray());
                Vector v = new Vector(vectorArray[0], vectorArray[1], vectorArray[2]);
                this.emeralds.add(new Emerald(v));
            }
        }

        JsonArray restocksJson = json.getAsJsonArray("restocks");
        if(restocksJson == null) return;
        for(int i = 0, size = restocksJson.size(); i < size; i++) {
            double[] vectorArray = JsonHelper.getArray(restocksJson.get(i).getAsJsonArray());
            Vector v = new Vector(vectorArray[0], vectorArray[1], vectorArray[2]);
            this.restocks.add(new Restock(v));
        }
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = super.getJSON();

        JsonObject capturepointJson = new JsonObject();
        for(CapturePoint point : capturePoints) {
            capturepointJson.add(point.getName(), wrapLocation(point.getVector()));
        }
        object.add("capturePoints", capturepointJson);

        JsonArray emeraldsArray =  new JsonArray();
        for(Emerald emerald : emeralds) {
            Vector location = emerald.getVector();
            emeraldsArray.add(wrapLocation(location));
        }
        object.add("emeralds", emeraldsArray);

        JsonArray restocksArray =  new JsonArray();
        for(Restock restock : restocks) {
            Vector location = restock.getVector();
            restocksArray.add(wrapLocation(location));
        }
        object.add("restocks", emeraldsArray);

        return object;
    }

    private JsonArray wrapLocation(Vector location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return JsonHelper.wrapXYZ(new int[]{x, y, z});
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
    public List<Vector> getRedSpawn() {
        return getSpawns(0);
    }
    public List<Location> getRedSpawnLoc() {
        List<Location> locations = new ArrayList<>();
        for(Vector v : getRedSpawn()) locations.add(v.toLocation(getGameWorld()));
        return locations;
    }

    @Override
    public List<Vector> getBlueSpawn() {
        return getSpawns(1);
    }
    public List<Location> getBlueSpawnLoc() {
        List<Location> locations = new ArrayList<>();
        for(Vector v : getBlueSpawn()) locations.add(v.toLocation(getGameWorld()));
        return locations;
    }
    private List<Vector> getSpawns(int index) {
        List<Vector> spawns = new ArrayList<>();
        double[][] teamSpawns = this.spawns.get(index);
        for(int i = 0, size = teamSpawns.length; i < size; i++) {
            double[] spawnArray = teamSpawns[i];
            Vector vector = new Vector(spawnArray[0], spawnArray[1], spawnArray[2]);
            spawns.add(vector);
        }
        return spawns;
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
