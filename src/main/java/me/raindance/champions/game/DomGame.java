package me.raindance.champions.game;

import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.map.MapManager;
import me.raindance.champions.Main;
import me.raindance.champions.game.map.DominateMap;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import com.podcrash.api.mc.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DomGame extends Game {
    private List<CapturePoint> capturePoints;
    private List<Emerald> emeralds;
    private List<Restock> restocks;
    private DomScoreboard scoreboard;
    private String actualWorld;
    public DomGame(int id, String name) {
        super(id, name, GameType.DOM);
        this.capturePoints = new ArrayList<>();
        this.emeralds = new ArrayList<>();
        this.restocks = new ArrayList<>();
    }

    @Override
    public int getMaxPlayers() {
        return 10;
    }

    @Override
    public GameScoreboard getGameScoreboard() {
        if(scoreboard == null) scoreboard = new DomScoreboard(this.getId());
        return scoreboard;
    }

    /**
     * Copy all the data from the original map towards the new copied map.
     * This is needed so that we don't delete the original map.
     * @see DominateMap
     */
    public void loadMap(){
        if(this.gameWorld == null) {
            log(String.format("%s: map is not dominate map", toString()));
            return;
        }
        MapManager.getMap(DominateMap.class, getMapName(), domMap -> {
            this.capturePoints = domMap.getCapturePoints();
            this.emeralds = domMap.getEmeralds();
            this.restocks = domMap.getRestocks();
            this.actualWorld = gameWorld.getName();
            World world = capturePoints.get(0).getWorld();
            for(CapturePoint capturePoint : capturePoints) {
                Location loc = capturePoint.getLocation();
                world.loadChunk(loc.getChunk());
            }
            GameScoreboard gameScoreboard;
            if((gameScoreboard = getGameScoreboard()) instanceof DomScoreboard) ((DomScoreboard) gameScoreboard).setup(this.capturePoints);
            setLoadedMap(true);
            // bukkit callback
        });
        //await
        try {
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                while (!isLoadedMap() && Main.getInstance().isEnabled()) {}
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void unloadWorld(){
        WorldManager.getInstance().deleteWorld(Bukkit.getWorld(actualWorld), true);
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

    public List<WinObjective> getWinObjectives() {
        return new ArrayList<WinObjective>(capturePoints);
    }

    public List<ItemObjective> getItemObjectives() {
        List<ItemObjective> itemObjectives = new ArrayList<>(emeralds);
        itemObjectives.addAll(restocks);
        return itemObjectives;
    }

    @Override
    public int getAbsoluteMinPlayers() {
        return 1;
    }

    @Override
    public Location spectatorSpawn() {
        return null;
    }

    @Override
    public void leaveCheck() {

    }

    @Override
    public TeamSettings getTeamSettings() {
        TeamSettings.Builder builder = new TeamSettings.Builder();
        return builder.setCapacity(5)
            .setMax(5)
            .setMin(1)
            .setTeamColors(TeamEnum.RED, TeamEnum.BLUE)
            .build();
    }

    //TODO
}
