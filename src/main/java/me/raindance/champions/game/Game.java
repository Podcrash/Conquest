package me.raindance.champions.game;


import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.raindance.champions.Main;
import me.raindance.champions.game.objects.ItemObjective;
import me.raindance.champions.game.objects.WinObjective;
import me.raindance.champions.game.resources.GameResource;
import me.raindance.champions.game.scoreboard.GameScoreboard;
import me.raindance.champions.util.PlayerCache;
import me.raindance.champions.util.Utility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Game {
    private int id;
    private List<GameResource> gameResources = new ArrayList<>();
    private String name;

    private List<Player> players;
    private List<Player> spectators;
    private List<Player> blueTeam;
    private List<Player> redTeam;

    protected List<Location> redSpawn;
    protected List<Location> blueSpawn;

    protected AtomicInteger redScore;
    protected AtomicInteger blueScore;

    private boolean isLoadedMap;
    private boolean ongoing = false;

    protected Scoreboard colorBoard;
    protected World gameWorld;

    private GameType type;


    public Game(int id, GameType type, String name) {
        this.id = id;
        this.name = name;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.blueTeam = new ArrayList<>();
        this.redTeam = new ArrayList<>();
        this.redSpawn = new ArrayList<>();
        this.blueSpawn = new ArrayList<>();
        this.redScore = new AtomicInteger(0);
        this.blueScore = new AtomicInteger(0);
        this.gameWorld = Bukkit.getWorld("Gulley");
        this.type = type;
    }

    public abstract GameScoreboard getGameScoreboard();
    public abstract void increment(String team, int score);

    public World getGameWorld() {
        return gameWorld;
    }
    public void setGameWorld(String string){
        this.gameWorld = Bukkit.getWorld(string);
    }

    public abstract void loadMap();
    public abstract void unloadWorld();

    public abstract List<WinObjective> getWinObjectives();
    public abstract List<ItemObjective> getItemObjectives();

    public String getMapName(){
        if(gameWorld == null) return "null";
        else return gameWorld.getName();
    }
    public boolean isLoadedMap() {
        return isLoadedMap || (gameWorld != null);
    }
    public void setLoadedMap(boolean loadedMap) {
        isLoadedMap = loadedMap;
    }

    public abstract int getMaxPlayers();
    public int getPlayerCount() {
        return this.blueTeam.size() + this.redTeam.size();
    }
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Add the player to specified team and set their scoreboard
     * @param player
     * @param color
     */
    public void addPlayerToTeam(Player player, String color) {
        if (color.equalsIgnoreCase("red")) {
            addPlayerToRed(player);
        } else if (color.equalsIgnoreCase("blue")) {
            addPlayerToBlue(player);
        } else throw new IllegalArgumentException(String.format("%s must be either red or blue!", color));
    }

    /**
     * Create a new scoreboard concerning redteam and blueteam
     */
    public void create(){
        Scoreboard colorBoard = getGameScoreboard().createBoard();
        String red = id + "redTeam";
        String blue = id + "blueTeam";
        if(colorBoard.getTeam(red) != null) {
            colorBoard.getTeam(red).unregister();
        }
        if(colorBoard.getTeam(blue) != null) {
            colorBoard.getTeam(blue).unregister();
        }
        Team redT = colorBoard.registerNewTeam(red);
        Team blueT = colorBoard.registerNewTeam(blue);
        redT.setPrefix(TeamEnum.RED.getChatColor().toString());
        blueT.setPrefix(TeamEnum.BLUE.getChatColor().toString());
    }
    public GameType getType() {
        return type;
    }

    private void addPlayerToRed(Player player) {
        this.redTeam.add(player);
        Scoreboard colorBoard = getGameScoreboard().getBoard();
        Team red = colorBoard.getTeam(id + "redTeam");
        player.setScoreboard(colorBoard);
        red.addEntry(player.getName());

    }
    private void addPlayerToBlue(Player player) {
        this.blueTeam.add(player);
        Scoreboard colorBoard = getGameScoreboard().getBoard();
        Team blue = colorBoard.getTeam(id + "blueTeam");
        player.setScoreboard(colorBoard);
        blue.addEntry(player.getName());
    }

    /**
     * If the player wants sto spectate, add them to the list.
     * If the game is ongoing, teleport them to the game
     * @param player
     */
    public void addSpectator(Player player) {
        this.spectators.add(player);
        this.players.add(player);
        player.setScoreboard(getGameScoreboard().getBoard());
        if(isOngoing()) {
            Location spawn = getRedSpawn().get(1);
            player.teleport(spawn);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
    public void removeSpectator(Player player) {
        this.spectators.remove(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        this.players.remove(player);
    }

    public Set<Player> getAllPlayers(){
        HashSet<Player> players = new HashSet<>();
        players.addAll(redTeam);
        players.addAll(blueTeam);
        players.addAll(this.players);
        return players;
    }
    public boolean contains(Player player) {
        return isRed(player) || isBlue(player) || isSpectating(player) || players.contains(player);
    }

    public List<Player> getTeamStr(String color) {
        if (color.equalsIgnoreCase("red")) {
            return redTeam;
        } else if (color.equalsIgnoreCase("blue")) {
            return blueTeam;
        } else throw new IllegalArgumentException(String.format("%s must be either red or blue!", color));
    }
    public List<Player> getBlueTeam() {
        return blueTeam;
    }
    public List<Player> getRedTeam() {
        return redTeam;
    }
    public List<Player> getSpectators() {return spectators; }

    public List<Player> getTeam(Player player) {
        if (redTeam.contains(player)) return redTeam;
        else if (blueTeam.contains(player)) return blueTeam;
        else return null;
    }
    public String getTeamColor(Player player) {
        if (redTeam.contains(player)) return "red";
        else if (blueTeam.contains(player)) return "blue";
        else return null;
    }

    public boolean isRed(Player player) {
        return redTeam.contains(player);
    }
    public boolean isBlue(Player player) {
        return blueTeam.contains(player);
    }
    public boolean isSpectating(Player player) {
        return spectators.contains(player);
    }

    public void registerResource(GameResource resource){
        if(resource.getGameID() != this.id) throw new IllegalArgumentException("resource does not correspond with its game id" + "gameid: " + id + " resourceid: " + resource.getGameID());
        gameResources.add(resource);
        resource.run(resource.getTicks(), resource.getDelayTicks());
    }
    public void registerResources(GameResource... resources){
        for (GameResource resource: resources) {
            registerResource(resource);
        }
    }
    public List<GameResource> getGameResources() {
        return gameResources;
    }
    public void unregisterGameResource(GameResource resource){
        resource.unregister();
        gameResources.remove(resource);
    }

    public boolean isOngoing() {
        return ongoing;
    }
    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public List<Location> getRedSpawn() {
        return redSpawn;
    }
    public List<Location> getBlueSpawn() {
        return blueSpawn;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    public void setBlueTeam(List<Player> blueTeam) {
        this.blueTeam = blueTeam == null ? new ArrayList<>() : blueTeam;
    }
    public void setRedTeam(List<Player> redTeam) {
        this.redTeam = redTeam == null ? new ArrayList<>() : redTeam;
    }

    public int getRedScore() {
        return redScore.get();
    }
    public int getBlueScore() {
        return blueScore.get();
    }

    private PlayerInfoData updateData(Player player, boolean reset) {
        TeamEnum team = TeamEnum.getByColor(getTeamColor(player));
        if(team == null) throw new IllegalArgumentException("This is not allowed");

        String display = reset ? player.getName() : team.getChatColor() + player.getName();
        WrappedChatComponent component = WrappedChatComponent.fromText(display);
        PlayerCache.getPlayerCache(player).setDisplayName(display);
        return new PlayerInfoData(WrappedGameProfile.fromPlayer(player), Utility.ping(player), EnumWrappers.NativeGameMode.SURVIVAL, component);
    }
    public void sendColorTab(boolean reset) {
        if(!reset) {
            /*
            for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                if(team.getName().equals(id + "redTeam") || team.getName().equals(id + "blueTeam")) {
                    WrapperPlayServerScoreboardTeam teamPacket = new WrapperPlayServerScoreboardTeam();
                    teamPacket.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED);
                    teamPacket.setPrefix(team.getPrefix());
                    teamPacket.setSuffix(team.getSuffix());
                    teamPacket.setPlayers(new ArrayList<>(team.getEntries()));
                    teamPacket.setDisplayName(team.getDisplayName());
                    teamPacket.setName(team.getName());
                    for(Player player : getPlayers()) teamPacket.sendPacket(player);
                    for(Player player : getSpectators()) teamPacket.sendPacket(player);
                }
            }
            */
        }else {
            for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                if(team.getName().equals(id + "redTeam") || team.getName().equals(id + "blueTeam")) {
                    Main.getInstance().log.info("cleared a team! from " + id);
                    team.unregister();
                }
            }
        }

    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            Team team = null;
            if (isRed(player)) {
                redTeam.remove(player);
                team = getGameScoreboard().getBoard().getTeam(id + "redTeam");
            } else if (isBlue(player)) {
                team = getGameScoreboard().getBoard().getTeam(id + "blueTeam");
                blueTeam.remove(player);
            } else if (isSpectating(player)) {
                spectators.remove(player);
            }
            if (team != null) {
                team.removeEntry(player.getName());
            }
            player.sendMessage(
                    String.format(
                            "%sChampions> %sYou were removed from Game #" + id + '!',
                            ChatColor.BLUE,
                            ChatColor.GRAY));
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    @Override
    public String toString() {
        return String.format("%s{Game %d}%s[%d/%d]%s:\n %s\n %s\n %s",ChatColor.GREEN, id, ChatColor.WHITE, getPlayerCount(), getMaxPlayers(), ChatColor.GRAY, niceLookingRed(), niceLookingBlue(), niceLookingSpec());
    }

    private String niceLookingRed() {
        StringBuilder result = new StringBuilder(ChatColor.RED + "" + ChatColor.BOLD + "Red Team: ");
        result.append(ChatColor.RESET);
        for(Player p : redTeam) {
            result.append(p.getName());
            result.append(' ');
        }
        return result.toString();
    }

    private String niceLookingBlue() {
        StringBuilder result = new StringBuilder(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team: ");
        result.append(ChatColor.RESET);
        for(Player p : blueTeam) {
            result.append(p.getName());
            result.append(' ');
        }
        return result.toString();
    }

    private String niceLookingSpec() {
        StringBuilder result = new StringBuilder(ChatColor.YELLOW + "" + ChatColor.BOLD + "Spectators: ");
        result.append(ChatColor.RESET);
        for(Player p : spectators) {
            result.append(p.getName());
            result.append(' ');
        }
        return result.toString();
    }

    public void broadcast(String msg) {
        for(Player player : getAllPlayers()){
            player.sendMessage(msg);
        }
    }
    protected final void log(String msg){
        Main.getInstance().getLogger().info(String.format("%s: %s", toString(), msg));
    }

    /**
     * Give an item that gives the details of a game.
     * The item will give:
     * - The id
     * - The map
     * - The current scores
     * - The players and spectators
     * Its status of ongoing or not will show in the form of its itemtype (redstone = ongoing, emerald = hasn't started)
     * {@link me.raindance.champions.inventory.MenuCreator#gamesMenu}
     * @return the item that will represent the game
     */
    public ItemStack getItemInfo() {
        ItemStack info = isOngoing() ? new ItemStack(Material.REDSTONE) : new ItemStack(Material.EMERALD);
        ItemMeta meta = info.getItemMeta();
        ChatColor color = isOngoing() ? ChatColor.RED : ChatColor.GREEN;
        meta.setDisplayName(color.toString() + ChatColor.BOLD + "Game " + id + ": " + type.getName());
        List<String> desc = new ArrayList<>();

        int remainder = getMaxPlayers() - getPlayerCount();
        desc.add(ChatColor.WHITE.toString() + getPlayerCount() + "/" + getMaxPlayers() + ": " + ChatColor.BOLD + Integer.toString(remainder) + " needed!");
        desc.add(ChatColor.YELLOW + "Map: " + getMapName());
        desc.add(ChatColor.WHITE + "Scores: ");
        desc.add(ChatColor.BOLD + ChatColor.RED.toString() + "Red: " + redScore.get());
        desc.add(ChatColor.BOLD + ChatColor.BLUE.toString() + "Blue: " + blueScore.get());

        desc.add("");
        desc.add(ChatColor.YELLOW + "Players:");
        for(Player player : players) {
            if(spectators.contains(player)) continue;
            TeamEnum team = TeamEnum.getByColor(getTeamColor(player));
            desc.add(team.getChatColor() + player.getName());
        }

        desc.add(" ");
        desc.add(ChatColor.WHITE + "Spectators: ");
        for(Player player : spectators) {
            desc.add(ChatColor.YELLOW + player.getName());
        }

        if(players.size() > 0) {
            info.addEnchantment(Main.customEnchantment, 1);
        }
        meta.setLore(desc);
        info.setItemMeta(meta);
        return info;
    }

    /**
     * If you left click a game item (the emeralds), it will do the following actions.
     * if ongoing, spectate
     * else join, if already in, leave
     * {@link me.raindance.champions.listeners.InventoryListener#clickItem(InventoryClickEvent)}
     * @param player
     */
    public void leftClickAction(Player player) {
        if(isOngoing())
            GameManager.addSpectator(id, player);
        else
            if(players.contains(player))
                GameManager.removePlayer(id, player);
            else
                GameManager.addPlayer(id, player);
    }

    public void rightClickAction(Player player) {
        GameManager.addSpectator(id, player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return id == game.id &&
                Objects.equals(name, game.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
