package me.raindance.champions.game;

import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.CapturePointPojo;
import com.podcrash.api.db.pojos.map.ConquestMap;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import me.raindance.champions.kits.skills.hunter.Rest;
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

    @Override
    public String getMode() {
        return "conquest";
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
        return getGameWorld().getSpawnLocation();
    }

    @Override
    public void leaveCheck() {

    }

    @Override
    public Class<? extends BaseMap> getMapClass() {
        return ConquestMap.class;
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

    public void setCapturePoints(List<CapturePointPojo> capturePoints) {
        List<CapturePoint> points = new ArrayList<>();
        for(CapturePointPojo pojo : capturePoints) {
            points.add(new CapturePoint(pojo));
        }
        this.capturePoints = points;
    }

    public void setEmeralds(List<Point> emeralds) {
        List<Emerald> points = new ArrayList<>();
        for(Point pojo : emeralds) {
            points.add(new Emerald(pojo));
        }
        this.emeralds = points;
    }

    public void setRestocks(List<Point> restocks) {
        List<Restock> points = new ArrayList<>();
        for(Point pojo : restocks) {
            points.add(new Restock(pojo));
        }
        this.restocks = points;
    }

    //TODO
}
