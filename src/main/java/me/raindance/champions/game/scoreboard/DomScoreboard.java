package me.raindance.champions.game.scoreboard;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameType;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import me.raindance.champions.game.DomGame;
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
    private static HashMap<String, Integer> scoreHash = new HashMap<>();
    //this is the scoreboard that we are going to be using.
    private String[] scores;

    public DomScoreboard(int gameId) {
        super(gameId, GameType.DOM);
        this.scores = new String[]{
                " ",
                ChatColor.BOLD.toString() + ChatColor.BLUE + "Blue", //9
                "0 ", //8
                "  ",
                ChatColor.BOLD.toString() + ChatColor.RED + "Red", //7
                "0", //6
                "", //5
                "cp1", //4
                "cp2", //3
                "cp3", //2
                "cp4", //1
                "cp5" //0
        };



    }

    /**
     * Make the scoreboard.
     * An example of this can be seen in the constructor
     * @param capturePoints the list of capturepoints
     */
    public void setup(List<CapturePoint> capturePoints) {
        List<String> points = new ArrayList<>();
        points.add("");
        points.add(ChatColor.RED + "Red");
        points.add("");
        points.add(ChatColor.BLUE + "Blue");
        for(CapturePoint point : capturePoints) {
            points.add(point.getName());
        }
        //convertScoreboard(points);
    }

    /**
     * Update the dom scoreboard values.
     */
    public void update(){
        Game game = GameManager.getGame();
        updateBlueScore(game.getBlueScore());
        updateRedScore(game.getRedScore());
    }
    private void updateRedScore(int score){
        this.scores[5] = ChatColor.BOLD + Integer.toString(score);
    }
    private void updateBlueScore(int score){
        this.scores[2] = ChatColor.BOLD + Integer.toString(score) + " ";
    }

    /**
     * Update if the point is captured
     * @param teamColor the color in which the point was captured.
     * @param capturePointName the name used to query to update
     */
    public void updateCapturePoint(String teamColor, String capturePointName){
        TeamEnum team = TeamEnum.getByColor(teamColor);
        if(team == null) return;
        for(int i = scores.length - 5; i < this.scores.length; i++){
            if(this.scores[i].contains(capturePointName)){
                this.scores[i] = team.getChatColor() + capturePointName;
            }
        }
    }

    /**
     * Update if a player of a team color is standing on a point.
     * It is represented by a colored star character.
     * @param teamColor the color in which the point was captured.
     * @param capturePointName the name used to query to update
     */
    public void updateCurrentlyInCPoint(String teamColor, String capturePointName) {
        TeamEnum team = TeamEnum.getByColor(teamColor);
        char star = '\u2605';
        String add = (team == null) ? "" : team.getChatColor().toString() + ChatColor.BOLD + star;
        for(int i = scores.length - 5; i < this.scores.length; i++){
            if(this.scores[i].contains(capturePointName)){
                for(CapturePoint cPoint : capturePoints) {
                    if(cPoint.getName().contains(capturePointName)) {
                        this.scores[i] = cPoint.getTeamColor().getChatColor() + cPoint.getName() + add;
                        return;
                    }
                }
            }
        }
    }
}
