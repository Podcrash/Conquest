package me.raindance.champions.game.resource;

import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.TeamEnum;
import com.podcrash.api.game.objects.objectives.CapturePoint;
import com.podcrash.api.game.resources.TimeGameResource;
import me.raindance.champions.game.DomGame;

public class CapturePointScorer extends TimeGameResource {
    private CapturePoint[] capturePoints;
    public CapturePointScorer(int gameID) {
        super(gameID, 10,100);
        capturePoints = ((DomGame) game).getCapturePoints().toArray(new CapturePoint[0]);
    }

    /**
     * For every capture point captured by a specific team,
     * Add 4 to the team score.
     */
    private void increment() {
        for(CapturePoint capturePoint : capturePoints){
            if(capturePoint.isCaptured()){
                TeamEnum color = capturePoint.getTeamColor();
                game.increment(color, 4); //is this inefficient?
            }
        }
    }

    /**
     * (Biased towards the red team)
     * If the score reaches over 15000, end the game and show scores.
     */
    private void checkOver15000() {
        for(GTeam team : game.getTeams()) {
            if(team.getScore() >= 15000) {
                GameManager.endGame(game);
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
