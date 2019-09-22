package me.raindance.champions.game;

import me.raindance.champions.Main;
import me.raindance.champions.events.game.GameEndEvent;
import me.raindance.champions.events.game.GameStartEvent;
import me.raindance.champions.game.objects.IObjective;
import me.raindance.champions.game.objects.ItemObjective;
import me.raindance.champions.game.objects.WinObjective;
import me.raindance.champions.game.resources.GameResource;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton - Handles games
 * @see me.raindance.champions.listeners.maintainers.GameListener
 */
public class GameManager {
    private static int gameID = 0;
    private static Game currentGame;

    public static Game createGame(String name, GameType type) {
        if(currentGame != null) return currentGame;
        Game game = null;
        switch (type){
            case DOM:
                game = new DomGame(gameID++, name);
                break;
            case CTF:
                break;
            case TDM:
                break;
        }
        if(game == null) throw new IllegalArgumentException("only the Dom GameType works for now");
        currentGame = game;
        game.create();
        game.setGameWorld("GulleyRevamp");
        return game;
    }
    public static void setGameMap(String worldName) {
        currentGame.setGameWorld(worldName);
    }

    public static void addSpectator(Player p) {
        Game game = currentGame;
        if(GameManager.hasPlayer(p)) {
            game.removeSpectator(p);
            p.sendMessage(String.format(
                    "%sChampions> %sYou are no longer spectating this game!",
                    ChatColor.BLUE,
                    ChatColor.GRAY));
            if(!p.getWorld().getName().equals("world")) {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }
            return;
        }
        if(!game.contains(p)) {
            game.addSpectator(p);
            p.sendMessage(
                    String.format(
                            "%sChampions> %sYou are now spectating %sGame %s%s.",
                            ChatColor.BLUE,
                            ChatColor.GRAY,
                            ChatColor.GREEN,
                            game.getId(),
                            ChatColor.GRAY));
        } else {
            p.sendMessage(
                    String.format(
                            "%sChampions> %sYou are already in this game.",
                            ChatColor.BLUE,
                            ChatColor.GRAY));
        }
    }

    public static boolean isSpectating(Player player){
        return currentGame != null && currentGame.isSpectating(player);
    }

    public static void addPlayer(Player p) {
        Game game = currentGame;
        if(GameManager.hasPlayer(p)) {
                p.sendMessage(String.format(
                        "%sChampions> %sYou are already in a game!",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
                return;
        }
        if(!game.contains(p)) {
            p.sendMessage(
                    String.format(
                            "%sChampions> %sYou were added to %sGame %s%s.",
                            ChatColor.BLUE,
                            ChatColor.GRAY,
                            ChatColor.GREEN,
                            game.getId(),
                            ChatColor.GRAY));

            ItemStack red = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
            ItemStack blue = new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData());

            ItemMeta meta2 = red.getItemMeta();
            meta2.setDisplayName(ChatColor.BOLD + ChatColor.RED.toString() + "Switch to Red Team!");
            red.setItemMeta(meta2);

            ItemMeta meta3 = blue.getItemMeta();
            meta3.setDisplayName(ChatColor.BOLD + ChatColor.BLUE.toString() + "Switch to Blue Team!");
            blue.setItemMeta(meta3);

            Inventory inventory = p.getInventory();
            inventory.setItem(1, red);
            inventory.setItem(2, blue);

            randomTeam(p);
            game.add(p);
        }else p.sendMessage(
                String.format(
                        "%sChampions> %sYou are already in the game.",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
        if (game.getMaxPlayers() == game.getPlayerCount()) {
            startGame();
        }
    }
    public static void removePlayer(Player p) {
        Game game = currentGame;
        game.removePlayer(p);

        Inventory inventory = p.getInventory();
        inventory.setItem(1, null);
        inventory.setItem(2, null);
    }
    public static boolean hasPlayer(Player p) {
        return currentGame != null && currentGame.contains(p);
    }

    public static void randomTeam(Player player) {
        Game game = currentGame;
        int blue = game.blueSize();
        int red = game.redSize();
        if(blue > red)
            joinTeam(player, "red");
        else if(red > blue)
            joinTeam(player, "blue");
        else //they are equal, good-ol RNG!
            joinTeam(player, new String[]{"red","blue"}[(int) (Math.random() + 0.5)]);

    }
    public static void joinTeam(Player player, String color) {
        Game game = currentGame;
        if (hasPlayer(player)) {
            if(game.getTeamColor(player) != null && game.getTeamColor(player).equalsIgnoreCase(color)) {
                player.sendMessage(String.format(
                        "%sChampions> %sYou are already on this team%s!",
                        ChatColor.BLUE,
                        ChatColor.GRAY,
                        ChatColor.GRAY));
                return;
            }
            if (game.getRedTeam().contains(player)) {
                game.removePlayerRed(player);
                player.sendMessage(
                        String.format(
                                "%sChampions> %sYou left the %sRed Team%s.",
                                ChatColor.BLUE,
                                ChatColor.GRAY,
                                ChatColor.RED,
                                ChatColor.GRAY));
            }else if (game.getBlueTeam().contains(player)) {
                game.removePlayerBlue(player);
                player.sendMessage(
                        String.format(
                                "%sChampions> %sYou left the %sBlue Team%s.",
                                ChatColor.BLUE,
                                ChatColor.GRAY,
                                ChatColor.BLUE,
                                ChatColor.GRAY));
            }
            if (color.equalsIgnoreCase("red") || color.equalsIgnoreCase("blue")) {
                game.addPlayerToTeam(player, color);
                TeamEnum team = TeamEnum.getByColor(color);
                player.sendMessage(
                        String.format(
                                "%sChampions> %sYou joined the %s%s Team %sin %sGame %s%s.",
                                ChatColor.BLUE,
                                ChatColor.GRAY,
                                team.getChatColor(),
                                team.getName(),
                                ChatColor.GRAY,
                                ChatColor.GREEN,
                                game.getId(),
                                ChatColor.GRAY));
            }
        }
    }

    /**
     * @see me.raindance.champions.listeners.maintainers.GameListener#onStart(GameStartEvent)
     */
    public static void startGame() {
        if(currentGame == null) return;
        Game game = currentGame;
        if(game.isOngoing()) {
            return;
        }
        Main.getInstance().log.info("Attempting to start game " + game.getId());
        GameStartEvent gamestart = new GameStartEvent(game);
        game.setOngoing(true);
        Main.getInstance().getServer().getPluginManager().callEvent(gamestart);
    }
    public static void endGame(Game game) {
        Location spawnLoc = Bukkit.getWorld("world").getSpawnLocation();
        game.setOngoing(false);
        GameEndEvent gameend = new GameEndEvent(game, spawnLoc);
        currentGame = null;
        Main.getInstance().getServer().getPluginManager().callEvent(gameend);
        createGame(Long.toString(System.currentTimeMillis()), game.getType());
    }

    public static Game getGame() {
        return currentGame;
    }
    /**
     *
     * @param game The game in which you want the GameResource from.
     * @return the GameResources
     */
    public static List<GameResource> getGameResources(Game game){
        return game.getGameResources();
    }
    public static Scoreboard getScoreboard(Game game) {
        return game.getGameScoreboard().getBukkitBoard();
    }

    private GameManager() {

    }
}
