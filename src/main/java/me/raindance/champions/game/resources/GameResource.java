package me.raindance.champions.game.resources;

import me.raindance.champions.Main;
import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import me.raindance.champions.time.resources.TimeResource;

/**
 * This is used as helpers for games that have started.
 */
public abstract class GameResource implements TimeResource {
    private Game game;
    private int gameID;

    private int ticks;
    private int delayTicks;

    @Override //default: feel free to override
    public boolean cancel() {
        return (getGame() == null) || !getGame().isOngoing();
    }

    public GameResource(int gameID, int ticks, int delayTicks){
        this.gameID = gameID;
        this.game = GameManager.getGame();
        this.ticks = ticks;
        this.delayTicks = delayTicks;
    }
    public GameResource(int gameID) {
        this(gameID, 1, 0);
    }

    public int getGameID() {
        return gameID;
    }
    public Game getGame(){
        return game;
    }

    public int getTicks() {
        return ticks;
    }
    public int getDelayTicks() {
        return delayTicks;
    }

    protected final void log(String msg){
        Main.getInstance().getLogger().info(String.format("%s: %s", this.getClass().getSimpleName(), msg));
    }

    protected void clear() {
        this.gameID = -1;
        this.game = null;
        this.ticks = -1;
        this.delayTicks = -1;
    }
}
