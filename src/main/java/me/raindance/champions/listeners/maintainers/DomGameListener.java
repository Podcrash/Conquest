package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.db.pojos.map.ConquestMap;
import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.economy.Currency;
import com.podcrash.api.economy.EconomyHandler;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.events.DeathApplyEvent;
import com.podcrash.api.events.ItemObjectiveSpawnEvent;
import com.podcrash.api.events.game.*;
import com.podcrash.api.game.*;
import com.podcrash.api.game.objects.IObjective;
import com.podcrash.api.game.objects.ItemObjective;
import com.podcrash.api.game.objects.WinObjective;
import com.podcrash.api.game.objects.objectives.*;
import com.podcrash.api.game.resources.HealthBarResource;
import com.podcrash.api.game.resources.ItemObjectiveSpawner;
import com.podcrash.api.game.resources.ScoreboardRepeater;
import com.podcrash.api.game.scoreboard.GameScoreboard;
import com.podcrash.api.listeners.ListenerBase;
import com.podcrash.api.time.resources.TipScheduler;
import com.podcrash.api.util.VectorUtil;
import com.podcrash.api.plugin.PodcrashSpigot;
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
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DomGameListener extends ListenerBase {
    private static final Material[] nonInteractables = new Material[]{
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.HOPPER,
            Material.BED,
            Material.BED_BLOCK,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.CAKE,
            Material.CAKE_BLOCK,
            Material.ENDER_CHEST,
            Material.DISPENSER,
            Material.BREWING_STAND,
            Material.COMMAND,
            Material.BEACON,
            Material.ANVIL,
            Material.DROPPER,
            Material.WORKBENCH,
    };
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


    @EventHandler
    public void stateListener(GameStateEvent e) {
        TipScheduler scheduler = Main.getInstance().getTips();
        PodcrashSpigot.debugLog("state change");
        if(scheduler == null)
            return;

        switch (e.getState()) {
            case LOBBY:
                scheduler.reset();
                scheduler.run(30 * 20, 0);
                break;
            case STARTED:
                scheduler.reset();
                break;
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void filterDeathCauses(DeathApplyEvent e) {
        //wat
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
        //face towards a star buff.
        List<Star> star = game.getStars();
        if(star.size() != 0) {//make sure there are some stars
            Location randomBuffLoc = game.getStars().get(0).getLocation();
            game.getTeams().forEach(team -> team.getSpawns().forEach(spawn -> {
                randomBuffLoc.setY(spawn.getY() + 1); //to make the y directions not seem weird, just add the y value
                //+ 1 for head height
                spawn.setDirection(VectorUtil.fromAtoB(spawn, randomBuffLoc));
            }));
        }

        GameScoreboard gameScoreboard;
        if((gameScoreboard = game.getGameScoreboard()) instanceof DomScoreboard)
            ((DomScoreboard) gameScoreboard).setup(game.getCapturePoints());
    }

    @EventHandler
    public void itemObjectiveSpawn(ItemObjectiveSpawnEvent e) {
        if(e.getObjective() instanceof Star) {
            DomGame game = (DomGame) GameManager.getGame();
            game.getStarBuff().replaceLine(StarBuff.PREFIX + ChatColor.YELLOW + " Active");
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onStart(GameStartEvent e) {
        Game game = e.getGame();
        Main.getInstance().getLogger().info("game is " + game);
        if (e.getGame().size() < 1) {
            Main.instance.getLogger().info(String.format("Can't start game %d, not enough players!", game.getId()));
        }

        game.sendColorTab(false);
        CapturePointDetector capture = new CapturePointDetector(game.getId());
        game.registerResources(
                new ScoreboardRepeater(game.getId()),
                new ItemObjectiveSpawner(game.getId()),
                capture,
                new CapturePointScorer(capture),
                new HealthBarResource(game.getId())
        );

        for(Player p: game.getBukkitPlayers()) {
            ChampionsPlayer player = ChampionsPlayerManager.getInstance().getChampionsPlayer(p);
            player.restockInventory();
            player.resetCooldowns();
            StatusApplier.getOrNew(p).removeStatus(Status.values());
        }
    }


    @EventHandler
    public void onEnd(GameEndEvent e) {
        Communicator.publishLobby(Communicator.getCode() + " close");
        DomGame game1 = new DomGame(GameManager.getCurrentID(), Long.toString(System.currentTimeMillis()));
        EconomyHandler handler = PodcrashSpigot.getInstance().getEconomyHandler();
        if (!PodcrashSpigot.getInstance().hasPPLOwner()) {
            for(Player player : e.getGame().getBukkitPlayers()) {
                if(GameManager.isSpectating(player)) break;
                player.sendMessage(String.format("%s%sYou earned %s %s!\n ",
                        Currency.GOLD.getFormatting(), ChatColor.BOLD, e.getGame().getReward(player), Currency.GOLD.getName()));
            }
        }
        GameSettings oldSettings = e.getGame().getGameSettings();
        GameManager.destroyCurrentGame();
        GameManager.createGame(game1);
        game1.setGameSettings(oldSettings);

        for(Player player : Bukkit.getOnlinePlayers()) {
            GameManager.addPlayer(player);
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            StatusApplier.getOrNew(player).removeStatus(Status.values());
        }
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
                game.broadcast(builder.toString());
            }else {
                /*
                builder.append(team.getChatColor()).append(ChatColor.BOLD);
                builder.append(objective.getName()).append(" is now neutralized!");

                 */
            }
            //game.broadcast(builder.toString());
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
            //player.sendMessage(ChatColor.WHITE + ChatColor.BOLD.toString() + "You collected a star!");
            //game.broadcast(team.getChatColor() + player.getName() + " received the buff!");
            game.broadcast(String.format("%s%s%s has collected a star!", ChatColor.WHITE, ChatColor.BOLD, player.getName()));
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
                if(!((ICharge) skill).isMaxAtStart()) return;
                for(int i = 0; i < ((ICharge) skill).getMaxCharges(); i++) {
                    ((ICharge) skill).addCharge();
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent e) {
        // If a player tries to right-click interact with a block we don't want them to, cancel the event.
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = e.getClickedBlock();
            for(Material m : nonInteractables) {
                if(block.getType().equals(m)) e.setCancelled(true);
            }
        }

        // If a player tries to extinguish a fire, cancel the event.
        if(e.getClickedBlock() != null) {
            Location loc = e.getClickedBlock().getLocation();
            loc.setY(loc.getY() + 1);
            Material target = loc.getBlock().getType();
            if(target.equals(Material.FIRE)) e.setCancelled(true);
        }

        // If the player is spectating / re-spawning, cancel the event.
        if(GameManager.getGame().isRespawning(e.getPlayer()) || !GameManager.getGame().isParticipating(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void maintainArmour(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if(clicked != null && event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void manageItemFrames(PlayerInteractEntityEvent e) {
        if(e.getRightClicked() instanceof ItemFrame) e.setCancelled(true);
    }
}
