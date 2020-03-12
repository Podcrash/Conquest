package me.raindance.champions.game.resource;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.resources.GameResource;

public class CapturePointScorer extends GameResource {
    private CapturePoint[] capturePoints;
    public CapturePointScorer(CapturePointDetector detector) {
        super(detector.getGameID(), 10, detector.getDelayTicks());
        capturePoints = detector.getCapturePoints();
    }

    /**
     * For every capture point captured by a specific team,
     * Add 4 to the team score.
     */
    private void increment() {
        for(CapturePoint capturePoint : capturePoints){
            if(capturePoint.isCaptured()){
                TeamEnum color = capturePoint.getTeamColor();
                getGame().increment(color, 4); //is this inefficient?
            }
        }
    }

    /**
     * (Biased towards the red team)
     * If the score reaches over 15000, end the game and show scores.
     */
    private void checkOver15000() {
        for(GTeam team : getGame().getTeams()) {
            if(team.getScore() >= 15000) {
                GameManager.endGame(getGame());
                return;
            }
        }
    }

    @Override
    public void task() {
        increment();
        checkOver15000();
    }

    @Override
    public void cleanup() {
        clear();
        this.capturePoints = null;
    }
}
