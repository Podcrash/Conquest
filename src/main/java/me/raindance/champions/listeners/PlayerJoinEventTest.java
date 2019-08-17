package me.raindance.champions.listeners;

import me.raindance.champions.Main;
import me.raindance.champions.damage.HitDetectionInjector;
import me.raindance.champions.events.DeathApplyEvent;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.classes.Assassin;
import me.raindance.champions.kits.classes.Ranger;
import me.raindance.champions.kits.skills.AssassinSkills.ComboAttack;
import me.raindance.champions.kits.skills.AssassinSkills.Illusion;
import me.raindance.champions.kits.skills.BruteSkills.SeismicSlam;
import me.raindance.champions.kits.skills.GlobalSkills.BreakFall;
import me.raindance.champions.kits.skills.MageSkills.Rupture;
import me.raindance.champions.kits.skills.RangerSkills.*;
import me.raindance.champions.mob.CustomEntityFirework;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class PlayerJoinEventTest extends ListenerBase {
    public PlayerJoinEventTest(JavaPlugin plugin) {
        super(plugin);
    }
    private static final ItemStack beacon = new ItemStack(Material.BEACON);

    static {
        System.out.println("Test HEllo! player join! :]");
        ItemMeta meta1 = beacon.getItemMeta();
        meta1.setDisplayName(ChatColor.BOLD + ChatColor.WHITE.toString() + "DOM Game Selector");
        beacon.setItemMeta(meta1);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (p.getWorld().getName().equals("world")) {
            p.getInventory().setItem(0, beacon);
            Illusion pounce = new Illusion(p, 4);
            Agility agility = new Agility(p, 1);
            RopedArrow ropedArrow = new RopedArrow(p, 1);
            VitalitySpores spores = new VitalitySpores(p, 3);
            Longshot longshot = new Longshot(p, 3);
            BreakFall fall = new BreakFall(p, 1);
            List<Skill> rangerKit = Arrays.asList(pounce, agility, spores, longshot, ropedArrow);

            Rupture illusion = new Rupture(p, 5);
            Main.getInstance().getLogger().info(String.format("%s %d %d", illusion.getName(), illusion.getMaxLevel(), illusion.getLevel()));
            SeismicSlam flash = new SeismicSlam(p, 3);
            PinDown smokeArrow = new PinDown(p, 1);
            Overcharge smokeBomb = new Overcharge(p, 3);
            ComboAttack comboAttack = new ComboAttack(p, 3);
            List<Skill> assassinKit = Arrays.asList(illusion, flash, smokeArrow, smokeBomb, comboAttack, fall);  // Knight!
            ChampionsPlayer kit = (e.getPlayer().getName().equals("Dragonnaire")) ? new Assassin(p, rangerKit) : new Ranger(p, rangerKit);
            new HitDetectionInjector(e.getPlayer()).injectHitDetection();
            ChampionsPlayerManager.getInstance().addChampionsPlayer(kit);
            //adds the PermissionAttachment so permissions work on the players
            Main.getInstance().setupPermissions(p);

            //Spawn the Firework, get the FireworkMeta.

            //Our random generator
            Random r = new Random();

            FireworkEffect.Type type = FireworkEffect.Type.BALL;

            //Create our effect with this
            FireworkEffect effect = FireworkEffect.builder()
                    .flicker(r.nextBoolean())
                    .withColor(Color.WHITE)
                    .with(type).trail(r.nextBoolean())
                    .build();
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            CustomEntityFirework.spawn(p.getLocation(), effect, players.toArray(new Player[players.size()]));

        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ChampionsPlayerManager cm = ChampionsPlayerManager.getInstance();
        ChampionsPlayer cplayer = cm.getChampionsPlayer(p);
        if(cplayer != null)
            cm.removeChampionsPlayer(cplayer);
    }

    @EventHandler
    public void die(DeathApplyEvent event) {
        if(event.getPlayer().getWorld().getName().equals("world")) {
            event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(event.getDeathMessage()));
            event.getPlayer().setHealth(20.0D);
            event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
        }
    }
}
