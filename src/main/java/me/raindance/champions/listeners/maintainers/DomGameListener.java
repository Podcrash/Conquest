package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.db.pojos.map.ConquestMap;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.ItemObjectiveSpawnEvent;
import com.podcrash.api.mc.events.game.*;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.IObjective;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.game.objects.objectives.*;
import com.podcrash.api.mc.game.resources.HealthBarResource;
import com.podcrash.api.mc.game.resources.ItemObjectiveSpawner;
import com.podcrash.api.mc.game.resources.ScoreboardRepeater;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.db.redis.Communicator;
import me.raindance.champions.Main;
import me.raindance.champions.game.DomGame;
import me.raindance.champions.game.StarBuff;
import me.raindance.champions.game.resource.CapturePointDetector;
import me.raindance.champions.game.resource.CapturePointScorer;
import me.raindance.champions.game.scoreboard.DomScoreboard;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.iskilltypes.action.ICharge;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DomGameListener extends ListenerBase {
    public DomGameListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void regDamage(DamageApplyEvent event) {
        if(!(event.getAttacker() instanceof Player) && !(event.getVictim() instanceof Player)) return;
        ChampionsPlayer championsVictim = ChampionsPlayerManager.getInstance().getChampionsPlayer((Player) event.getVictim());
        ChampionsPlayer championsAttacker = ChampionsPlayerManager.getInstance().getChampionsPlayer((Player) event.getAttacker());

        event.setArmorValue(championsVictim.getArmorValue());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void mapLoad(GameMapLoadEvent event) {
        if(!(event.getMap() instanceof ConquestMap) || !(event.getGame() instanceof DomGame)) return;
        DomGame game = (DomGame) event.getGame();
        ConquestMap domMap = (ConquestMap) event.getMap();
        World world = event.getWorld();
        game.setCapturePoints(domMap.getCapturePointPojos());
        game.setDiamonds(domMap.getDiamonds());
        game.setRestocks(domMap.getRestocks());
        game.setLandmines(domMap.getMines());
        game.setStars(domMap.getStars());

        for (WinObjective winObjective : game.getWinObjectives()) {
            winObjective.setWorld(world);
        }

        for (ItemObjective itemObjective : game.getItemObjectives()) {
            itemObjective.setWorld(world);
        }


        GameScoreboard gameScoreboard;
        if((gameScoreboard = game.getGameScoreboard()) instanceof DomScoreboard)
            ((DomScoreboard) gameScoreboard).setup(game.getCapturePoints());
    }

    @EventHandler
    public void itemObjectiveSpawn(ItemObjectiveSpawnEvent e) {
        if(e.getObjective() instanceof Star) {
            DomGame game = (DomGame) GameManager.getGame();
            game.getStarBuff().replaceLine(StarBuff.PREFIX + " ACTIVE");
        }
    }
    @EventHandler
    public void onStart(GameStartEvent e) {
        Game game = e.getGame();
        game.broadcast(game.toString());
        Main.getInstance().getLogger().info("game is " + game);
        if (e.getGame().getPlayerCount() < 1) {
            Main.instance.getLogger().info(String.format("Can't start game %d, not enough players!", game.getId()));
        }
        String startingMsg = String.format("Game %d is starting up with map %s", e.getGame().getId(), e.getGame().getMapName());
        for(Player p : e.getGame().getBukkitPlayers()) p.sendMessage(startingMsg);

        game.sendColorTab(false);
        CapturePointDetector capture = new CapturePointDetector(game.getId());
        game.registerResources(
                new ScoreboardRepeater(game.getId()),
                new ItemObjectiveSpawner(game.getId()),
                capture,
                new CapturePointScorer(capture),
                new HealthBarResource(game.getId())
        );
        game.broadcast(e.getMessage());
    }


    @EventHandler
    public void onEnd(GameEndEvent e) {
        Communicator.publishLobby(Communicator.getCode() + " close");
        DomGame game1 = new DomGame(GameManager.getCurrentID(), Long.toString(System.currentTimeMillis()));
        GameManager.destroyCurrentGame();
        GameManager.createGame(game1);

        for(Player player : Bukkit.getOnlinePlayers())
            GameManager.addPlayer(player);
    }


    @EventHandler
    public void onGameDeath(GameDeathEvent e) {
        Player victim = e.getWho();
        TeamEnum victimTeam = e.getGame().getTeamEnum(victim);

        if(e.getKiller() instanceof Player) {
            TeamEnum enemyTeam = e.getGame().getTeamEnum((Player) e.getKiller());
            if (e.getWho() != e.getKiller())
                e.getGame().increment(enemyTeam, 50);
        }
        List<Skill> skills = ChampionsPlayerManager.getInstance().getChampionsPlayer(victim).getSkills();
        for(Skill skill : skills) {
            if(!(skill instanceof TogglePassive)) continue;
            if (((TogglePassive) skill).isToggled())
                ((TogglePassive) skill).forceToggle();
        }

        DomGame game = (DomGame) e.getGame();

        game.getStarBuff().collectorDiedNotify(victim);
    }

    @EventHandler
    public void ressurect(GameResurrectEvent e) {
        ChampionsPlayerManager.getInstance().getChampionsPlayer(e.getWho()).respawn();
    }

    @EventHandler
    public void onCapture(GameCaptureEvent e){
        Game game = e.getGame();
        IObjective objective = e.getObjective();
        if(objective instanceof CapturePoint){
            String teamColor = ((CapturePoint) objective).getColor();
            TeamEnum team = TeamEnum.getByColor(teamColor);
            StringBuilder builder = new StringBuilder();
            if(team != TeamEnum.WHITE) {
                builder.append(team.getChatColor());
                builder.append(ChatColor.BOLD);
                builder.append(team.getName());
                builder.append(" has captured ");
                builder.append(objective.getName());
                builder.append("!");
            }else {
                builder.append(team.getChatColor()).append(ChatColor.BOLD);
                builder.append(objective.getName()).append(" is now neutralized!");
            }
            game.broadcast(builder.toString());
            DomScoreboard scoreboard = (DomScoreboard) game.getGameScoreboard();
            scoreboard.updateCapturePoint(team, objective.getName());
            objective.spawnFirework();
        }else if(objective instanceof Diamond) {

        }else e.getWho().sendMessage(e.getMessage());
    }

    @EventHandler
    public void pickUp(GamePickUpEvent event) {
        ItemObjective itemObjective = event.getItem();
        Player player = event.getWho();
        DomGame game = (DomGame) event.getGame();
        TeamEnum team = game.getTeamEnum(player);
        if(itemObjective instanceof Diamond) {
            game.increment(team, 200);
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.AQUA);
            builder.append(ChatColor.BOLD);
            builder.append(team.getChatColor());
            builder.append(player.getName());
            //builder.append(ChatColor.GREEN);
            builder.append(" has collected 200 points!");
            game.broadcast(builder.toString());
        }else if(itemObjective instanceof Restock) {
            ChampionsPlayer cPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
            cPlayer.restockInventory();
            player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "You recieved supplies!");
        }else if(itemObjective instanceof Landmine) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You collected a landmine!");
            player.getInventory().addItem(new ItemStack(Material.TNT));
            game.increment(team, 50);
        }else if(itemObjective instanceof Star) {
            player.sendMessage(ChatColor.WHITE + ChatColor.BOLD.toString() + "You collected a star!");
            game.broadcast(team.getChatColor() + player.getName() + " received the buff!");
            StatusApplier.getOrNew(player).applyStatus(Status.STRENGTH, 30, 0, false, true);
            StatusApplier.getOrNew(player).applyStatus(Status.SPEED, 30, 0, false, true);
            StatusApplier.getOrNew(player).applyStatus(Status.REGENERATION, 30, 0, false, true);
            game.getStarBuff().setCollector(player);
            game.increment(team, 300);
        }
        itemObjective.spawnFirework();
        game.getGameResources().forEach(resource -> {
            if(resource instanceof ItemObjectiveSpawner){
                ((ItemObjectiveSpawner) resource).setItemTime(itemObjective, System.currentTimeMillis());
            }
        });
    }

    @EventHandler
    public void resurrect(GameResurrectEvent e) {
        ChampionsPlayer championsPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(e.getWho());
        championsPlayer.getSkills().forEach(skill -> {
            if(skill instanceof ICharge) {
                for(int i = 0; i < ((ICharge) skill).getMaxCharges(); i++) {
                    ((ICharge) skill).addCharge();
                }
            }
        });
    }
}
