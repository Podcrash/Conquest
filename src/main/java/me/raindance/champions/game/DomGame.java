package me.raindance.champions.game;

import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.CapturePointPojo;
import com.podcrash.api.db.pojos.map.ConquestMap;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.*;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class DomGame extends Game {
    private List<CapturePoint> capturePoints;
    private List<Diamond> diamonds;
    private List<Restock> restocks;
    private List<Star> stars;
    private List<Landmine> mines;

    private DomScoreboard scoreboard;
    private String actualWorld;

    private StarBuff starBuff;
    public DomGame(int id, String name) {
        super(id, name, GameType.DOM);
        this.capturePoints = new ArrayList<>();
        this.diamonds = new ArrayList<>();
        this.restocks = new ArrayList<>();
        this.stars = new ArrayList<>();
        this.mines = new ArrayList<>();

        this.starBuff = new StarBuff(this);
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

    public StarBuff getStarBuff() {
        return starBuff;
    }

    public List<CapturePoint> getCapturePoints() {
        return capturePoints;
    }
    public List<Diamond> getDiamonds() {
        return diamonds;
    }
    public List<Restock> getRestocks() {
        return restocks;
    }
    public List<Star> getStars() {
        return stars;
    }
    public List<Landmine> getMines() {
        return mines;
    }

    public List<WinObjective> getWinObjectives() {
        return new ArrayList<WinObjective>(capturePoints);
    }

    public List<ItemObjective> getItemObjectives() {
        List<ItemObjective> itemObjectives = new ArrayList<>(diamonds);
        itemObjectives.addAll(restocks);
        itemObjectives.addAll(stars);
        itemObjectives.addAll(mines);
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

    public void setDiamonds(List<Point> diamonds) {
        List<Diamond> points = new ArrayList<>();
        for(Point pojo : diamonds) {
            points.add(new Diamond(pojo));
        }
        this.diamonds = points;
    }

    public void setStars(List<Point> stars) {
        stars.forEach(star -> this.stars.add(new Star(star)));
    }

    public void setLandmines(List<Point> mines) {
        mines.forEach(mine -> this.mines.add(new Landmine(mine)));
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
