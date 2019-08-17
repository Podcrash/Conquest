package me.raindance.champions.game.resources;

import me.raindance.champions.game.GameManager;
import me.raindance.champions.game.TeamEnum;
import me.raindance.champions.game.objects.objectives.CapturePoint;

public class CapturePointScorer extends GameResource {
    private CapturePoint[] capturePoints;
    public CapturePointScorer(CapturePointDetector detector) {
        super(detector.getGameID(), detector.getTicks(), detector.getDelayTicks());
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
                getGame().increment(color.getName(), 4); //is this inefficient?
            }
        }
    }

    /**
     * (Biased towards the red team)
     * If the score reaches over 15000, end the game and show scores.
     */
    private void checkOver15000() {
        int[] scores = new int[]{getGame().getRedScore(), getGame().getBlueScore()};
        for(int score : scores){
            if(score >= 15000){
                GameManager.endGame(getGame());
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
