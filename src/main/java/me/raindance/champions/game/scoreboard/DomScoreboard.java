package me.raindance.champions.game.scoreboard;

import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import com.podcrash.api.plugin.Pluginizer;
import me.raindance.champions.game.DomGame;
import me.raindance.champions.game.StarBuff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This scoreboard is backed by an array to constantly show a dynamic changing scoreboard
 */
public class DomScoreboard extends GameScoreboard {
    private List<CapturePoint> capturePoints;

    public DomScoreboard(int gameId) {
        super(17, gameId, GameType.DOM);



    }

    /**
     * Make the scoreboard.
     * An example of this can be seen in the constructor
     * @param capturePoints the list of capturepoints
     */
    public void setup(List<CapturePoint> capturePoints) {
        List<String> points = new ArrayList<>();
        for(GTeam team : getGame().getTeams()) {
            TeamEnum teamE = team.getTeamEnum();
            points.add(teamE.getChatColor() + teamE.getName());
            points.add("");
        }
        points.add("");
        this.capturePoints = capturePoints;

        for(CapturePoint point : capturePoints) {
            points.add(point.getName());
        }

        points.add("");
        points.add(StarBuff.PREFIX + " ACTIVE");
        createGameScoreboard(ChatColor.LIGHT_PURPLE + "Conquest", "Control", points);
    }

    public void createScoreboard() {

    }
    /**
     * Update the dom scoreboard values.
     */
    public void update(){
        for(GTeam team : getGame().getTeams()) updateScore(team.getTeamEnum());
    }

    @Override
    public void startScoreboardTimer() {

    }

    public void updateScore(TeamEnum team) {
        List<String> lines = getLines();

        String lowerTeam = (team.getChatColor() + team.getName()).toLowerCase();
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            //Pluginizer.getLogger().info(line.toLowerCase()+ " vs " + lowerTeam);
            if(!line.toLowerCase().contains(lowerTeam)) continue;
            //prev is the exact same integer.
            setLine(i, String.valueOf(getGame().getTeam(team).getScore()));
            break;
        }
    }
    /**
     * Update if the point is captured
     * @param team the team in which the point was captured.
     * @param capturePointName the name used to query to update
     */
    public void updateCapturePoint(TeamEnum team, String capturePointName){
        List<String> lines = getLines();
        for(int i = 0; i < lines.size(); i++){
            String line = lines.get(i);
            if(!line.contains(capturePointName)) continue;
            setLine(i + 1, team.getChatColor() + capturePointName);
            break;
        }
    }

    /**
     * Update if a player of a team color is standing on a point.
     * It is represented by a colored star character.
     * @param team the color in which the point was captured.
     * @param capturePointName the name used to query to update
     */
    public void updateCurrentlyInCPoint(TeamEnum team, String capturePointName) {
        char star = '\u2605';
        String add = (team == null) ? "" : team.getChatColor().toString() + ChatColor.BOLD + star;
        List<String> lines = getLines();

        String newLine = ""; //never happens.
        for(CapturePoint capturePoint : capturePoints) {
            if(capturePoint.getName().contains(capturePointName)) {
                newLine = capturePoint.getTeamColor().getChatColor() + capturePoint.getName() + add;
                break;
            }
        }
        if(newLine.isEmpty()) return;
        for(int i = 0; i < lines.size(); i++){
            String line = lines.get(i);
            if(!line.contains(capturePointName)) continue;
            setLine(i + 1, newLine);
            break;
        }
    }
}
