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
    public static final int MAX_GAME_COUNT = 9;
    private static final List<Game> games = new ArrayList<>();
    private static final List<Player> queue = new ArrayList<>();

    public static int getGameCount() {
        return games.size();
    }
    public static boolean anyGames() {
        return games.size() > 0;
    }
    public static List<Game> getGames() {
        return games;
    }

    public static Game createGame(String name, GameType type) {
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
        games.add(game);
        game.create();
        game.setGameWorld("GulleyRevamp");
        return game;
    }
    public static void setGameMap(Game game, String worldName) {
        game.setGameWorld(worldName);
    }

    public static void addSpectator(int id, Player p) {
        Game game = getGame(id);
        if(GameManager.hasPlayer(p)) {
            if(game == GameManager.getGame(p)) {
                game.removeSpectator(p);
                p.sendMessage(String.format(
                        "%sChampions> %sYou are no longer spectating this game!",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
                if(!p.getWorld().getName().equals("world")) {
                    p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                }
            }else {
                p.sendMessage(String.format(
                        "%sChampions> %sYou are already spectating in another game!",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
            }
            return;
        }
        if(!game.getPlayers().contains(p)) {
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
        Game game = getGame(player);
        if(game != null) {
            return game.isSpectating(player);
        } else {
            return false;
        }
    }

    public static void addPlayer(int id, Player p) {

        Game game = getGame(id);
        if(GameManager.hasPlayer(p)) {
                p.sendMessage(String.format(
                        "%sChampions> %sYou are already in a game!",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
                return;
        }
        if(!game.getPlayers().contains(p)) {
            game.getPlayers().add(p);
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
        }else p.sendMessage(
                String.format(
                        "%sChampions> %sYou are already in the game.",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
        if (game.getMaxPlayers() == game.getPlayerCount()) {
            startGame(game);
        }
    }
    public static void removePlayer(int id, Player p) {
        Game game = getGame(id);
        game.removePlayer(p);

        Inventory inventory = p.getInventory();
        inventory.setItem(1, null);
        inventory.setItem(2, null);
    }
    public static boolean hasPlayer(Player p) {
        for (Game game : games) {
            if (game.contains(p)) return true;
        }
        return false;
    }

    public static void joinTeam(Player player, int id, String color) {
        Game game = getGame(id);
        if (game.getPlayers().contains(player)) {
            if(game.getTeamColor(player) != null && game.getTeamColor(player).equalsIgnoreCase(color)) {
                player.sendMessage(String.format(
                        "%sChampions> %sYou are already on this team%s!",
                        ChatColor.BLUE,
                        ChatColor.GRAY,
                        ChatColor.GRAY));
                return;
            }
            if (game.getRedTeam().contains(player)) {
                game.getRedTeam().remove(player);
                player.sendMessage(
                        String.format(
                                "%sChampions> %sYou left the %sRed Team%s.",
                                ChatColor.BLUE,
                                ChatColor.GRAY,
                                ChatColor.RED,
                                ChatColor.GRAY));
            }else if (game.getBlueTeam().contains(player)) {
                game.getBlueTeam().remove(player);
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
     * @param game
     */
    public static void startGame(Game game) {
        if(game == null) return;
        if(game.isOngoing()) {
            return;
        }
        Main.getInstance().log.info("Attempting to start game " + game.getId());
        GameStartEvent gamestart = new GameStartEvent(game);
        game.setOngoing(true);
        Main.getInstance().getServer().getPluginManager().callEvent(gamestart);
    }
    public static void endGame(Game game) {
        Location spawnLoc = new Location(Bukkit.getWorld("world"), 0, 100, 0);
        game.setOngoing(false);
        GameEndEvent gameend = new GameEndEvent(game, spawnLoc);
        games.remove(game);
        Main.getInstance().getServer().getPluginManager().callEvent(gameend);
        createGame(Long.toString(System.currentTimeMillis()), game.getType());
    }

    public static Game getGame(int id) {
        for (Game game : games) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }
    public static Game getGame(Player p) {
        for (Game game : games) {
            if (game.contains(p)) return game;
        }
        return null;
    }

    public static Game getGame(IObjective objective) {
        for(Game game : games) {
            if(objective instanceof ItemObjective && game.getItemObjectives().contains(objective)) return game;
            if(objective instanceof WinObjective && game.getWinObjectives().contains(objective)) return game;
        }
        return null;
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
