package me.raindance.champions.game;

import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.map.BaseGameMap;
import me.raindance.champions.game.map.DominateMap;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

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
    public Class<? extends BaseGameMap> getMapClass() {
        return DominateMap.class;
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

    public void setCapturePoints(List<CapturePoint> capturePoints) {
        this.capturePoints = capturePoints;
    }

    public void setEmeralds(List<Emerald> emeralds) {
        this.emeralds = emeralds;
    }

    public void setRestocks(List<Restock> restocks) {
        this.restocks = restocks;
    }

    //TODO
}
