package me.raindance.champions.game.resources;

import me.raindance.champions.events.game.GameCaptureEvent;
import me.raindance.champions.game.DomGame;
import me.raindance.champions.game.TeamEnum;
import me.raindance.champions.game.objects.objectives.CapturePoint;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CapturePointDetector extends GameResource {
    private final CapturePoint[] capturePoints;
    private final boolean[] playersCurrentlyIn;
    private DomScoreboard scoreboard;
    /**
     * > 0 = red
     * < 0 = blue
     * = 0 = white
     */
    private final Map<Integer, Integer> teamToColor = new HashMap<>();
    /**
     * [capturePoint index][x,y,z coordinate][first or second bound]
     */
    private final double[][][] bounds;
    private final Player[] players;

    public CapturePointDetector(int gameID) {
        super(gameID, 10, 100);
        this.capturePoints = ((DomGame) getGame()).getCapturePoints().toArray(new CapturePoint[((DomGame) getGame()).getCapturePoints().size()]);
        this.bounds = new double[5][3][2];
        this.playersCurrentlyIn = new boolean[5];
        for (int i = 0; i < this.capturePoints.length; i++) {
            playersCurrentlyIn[i] = false;
            Location[] cbounds = this.capturePoints[i].getBounds();
            teamToColor.put(i, 0);
            for (int b = 0; b < cbounds.length; b++) {
                this.bounds[i][0][b] = cbounds[b].getX();
                this.bounds[i][1][b] = cbounds[b].getY();
                this.bounds[i][2][b] = cbounds[b].getZ();
            }
        }
        List<Player> players = new ArrayList<>(getGame().getRedTeam());
        players.addAll(getGame().getBlueTeam());
        this.players = players.toArray(new Player[players.size()]);
        this.scoreboard = ((DomScoreboard) getGame().getGameScoreboard());
    }

    public CapturePoint[] getCapturePoints() {
        return capturePoints;
    }

    /**
     * If the player is within bound of a specified capture point.
     * @param i the index of the capture point
     * @param player the player
     * @return whether the player is within the bound
     */
    private boolean isInBound(int i, Player player) {
        Location location = player.getLocation();
        double x = location.getBlockX();
        double y = location.getBlockY();
        double z = location.getBlockZ();
        double[][] cpoint = this.bounds[i];
        if (cpoint[0][1] <= x && x <= cpoint[0][0]){
            if (cpoint[1][0] <= y && y <= cpoint[1][1]) {
                if (cpoint[2][1] <= z && z <= cpoint[2][0]) {
                    if(!player.getAllowFlight()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @see {@link CapturePointDetector#task()}
     * If a player is currently in a capture point (using {@link CapturePointDetector#isInBound(int, Player)})
     * Then add them to the team color, as well as update if a player is in it.
     * @param i the capture point index
     */
    private void findPlayerInCap(int i) {
        for(int p = 0; p < players.length; p++){
            boolean a = isInBound(i, players[p]);
            if(a) {
                String color = getGame().getTeamColor(players[p]);
                teamToColor.put(i, teamToColor.get(i) + TeamEnum.getByColor(color).getIntData());
                playersCurrentlyIn[i] = true;
            }else playersCurrentlyIn[i] = false;
        }
    }

    /**
     * Capture the point if there are players in it.
     * Positive = red
     * Negative = blue
     * if there is nobody on the capture point, just neutralize it {@link CapturePoint#neutralize()}
     * else capture the point {@link CapturePoint#capture(String)}
     * If the point becomes captured, then call the GameCaptureEvent {@link GameCaptureEvent)
     * @param i the capture point index
     */
    private void capture(int i) {
        CapturePoint capturePoint = capturePoints[i];
        int times = teamToColor.get(i);
        TeamEnum team = null;
        if(times > 0){
            scoreboard.updateCurrentlyInCPoint(TeamEnum.RED.getName(), capturePoint.getName());
            team = capturePoint.capture(TeamEnum.RED.getName(), times);
        }else if(times < 0){
            scoreboard.updateCurrentlyInCPoint(TeamEnum.BLUE.getName(), capturePoint.getName());
            team = capturePoint.capture(TeamEnum.BLUE.getName(), times * -1);
        }else {
            if(capturePoint.getTeamColor() == TeamEnum.WHITE && capturePoint.isFull()) return;
            if(!playersCurrentlyIn[i]) {
                scoreboard.updateCurrentlyInCPoint(null, capturePoint.getName());
                capturePoint.neutralize();
            }
        }
        teamToColor.put(i, 0);
        if(team != null) Bukkit.getPluginManager().callEvent(new GameCaptureEvent(getGame(), null, capturePoint));
    }
    @Override
    public void task() {
        for(int i = 0; i < capturePoints.length; i++) {
            findPlayerInCap(i);
            capture(i);
        }
    }


    @Override
    public void cleanup() {
        clear();
    }
}
