package me.raindance.champions.game;

import me.raindance.champions.game.map.types.DominateMap;
import me.raindance.champions.game.objects.ItemObjective;
import me.raindance.champions.game.objects.WinObjective;
import me.raindance.champions.game.objects.objectives.CapturePoint;
import me.raindance.champions.game.objects.objectives.Emerald;
import me.raindance.champions.game.objects.objectives.Restock;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import me.raindance.champions.game.scoreboard.GameScoreboard;
import me.raindance.champions.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class DomGame extends Game {
    private List<CapturePoint> capturePoints;
    private List<Emerald> emeralds;
    private List<Restock> restocks;
    private DomScoreboard scoreboard;
    private String actualWorld;
    public DomGame(int id, String name) {
        super(id, GameType.DOM, name);
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
     * Update the score values.
     * @param team "red" or "blue"
     * @param score the increment to add by
     */
    public void increment(String team, int score){
        if((team.equalsIgnoreCase("blue"))){
            this.blueScore.set(getBlueScore() + score);
        }else if(team.equalsIgnoreCase("red")){
            this.redScore.set(getRedScore() + score);
        }else throw new IllegalArgumentException("team is not blue or red! It is: " + team);
        getGameScoreboard().update();
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
        final String name = this.gameWorld.getName();
        World copy = WorldManager.getInstance().copyWorld(name);
        DominateMap domMap = new DominateMap(copy, name);
        this.capturePoints = domMap.getCapturePoints();
        this.emeralds = domMap.getEmeralds();
        this.restocks = domMap.getRestocks();
        this.redSpawn = domMap.getRedSpawn();
        this.blueSpawn = domMap.getBlueSpawn();
        this.actualWorld = copy.getName();
        for(CapturePoint capturePoint : capturePoints) {
            capturePoint.getLocation().getWorld().loadChunk(capturePoint.getLocation().getChunk());
        }
        GameScoreboard gameScoreboard;
        if((gameScoreboard = getGameScoreboard()) instanceof DomScoreboard) ((DomScoreboard) gameScoreboard).setup(this.capturePoints);
        setLoadedMap(true);
        this.gameWorld = copy;
        log("Loaded world " + name);
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

    @Override
    public List<WinObjective> getWinObjectives() {
        return new ArrayList<WinObjective>(capturePoints);
    }

    @Override
    public List<ItemObjective> getItemObjectives() {
        List<ItemObjective> itemObjectives = new ArrayList<>(emeralds);
        itemObjectives.addAll(restocks);
        return itemObjectives;
    }

    //TODO
}
