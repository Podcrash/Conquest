package me.raindance.champions.game;

import com.abstractpackets.packetwrapper.ILocationPacket;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Helper module to handle buffed players
 */
public class StarBuff implements TimeResource {
    public static final String PREFIX = ChatColor.WHITE + "" + ChatColor.BOLD + "STAR:" + ChatColor.RESET + "" + ChatColor.BOLD + "" + ChatColor.YELLOW;
    private Game game;
    private GameScoreboard scoreboard;
    private String holder;
    private boolean dead;
    private long endTime;

    public StarBuff(Game game) {
        this.game = game;
        this.scoreboard = game.getGameScoreboard();
        this.dead = false;
    }

    public void replaceLine(String line) {
        List<String> lines = scoreboard.getLines();
        for(int i = 0; i < lines.size(); i++) {
            String curr = lines.get(i);
            if(!curr.toLowerCase().contains("star")) continue;
            scoreboard.setLine(i + 1, line);
            break;
        }
    }

    public void setCollector(Player collector) {
        this.holder = collector.getName();
        this.endTime = System.currentTimeMillis() + 1000L * 30;
        replaceLine(PREFIX + " " + holder);
        runAsync(1, 0);
    }

    public LivingEntity getCollector() {
        if(holder == null) return null;
        return Bukkit.getPlayer(holder);
    }
    public void collectorDiedNotify(Player died) {
        if(!died.getName().equalsIgnoreCase(holder)) return;
        this.dead = true;
    }

    @Override
    public void task() {
        //give the buffed player some particle effects
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(EnumWrappers.Particle.VILLAGER_HAPPY, 3);
        Player owner = Bukkit.getPlayer(holder);

        List<Status> effects = StatusApplier.getOrNew(owner).getEffects();
        long timeLeft = endTime - System.currentTimeMillis();
        timeLeft /= 1000L; //Convert to seconds

        //Make sure the effects are there (incase other stuff gives stronger regen but ends)
        if (!effects.contains(Status.STRENGTH) && timeLeft > 0.5) {
            StatusApplier.getOrNew(owner).applyStatus(Status.STRENGTH, timeLeft, 0, false, true);
        }
        if (!effects.contains(Status.RESISTANCE) && timeLeft > 0.5) {
            StatusApplier.getOrNew(owner).applyStatus(Status.RESISTANCE, timeLeft, 0, false, true);
        }
        if (!effects.contains(Status.REGENERATION) && timeLeft > 0.5) {
            StatusApplier.getOrNew(owner).applyStatus(Status.REGENERATION, timeLeft, 0, false, true);
        }


        Location location = owner.getLocation().add(0, 1.25, 0);
        packet.setLocation(location);
        PacketUtil.asyncSend(packet, location.getWorld().getPlayers());
    }

    @Override
    public boolean cancel() {
        return dead || System.currentTimeMillis() > endTime;
    }

    @Override
    public void cleanup() {
        TeamEnum team = game.getTeamEnum(Bukkit.getPlayer(holder));
        TeamEnum oppoTeam = getOppositeTeam(team);
        if(dead) {
            game.increment(oppoTeam, 300);
            game.broadcast(team.getChatColor() + holder + " lost the buff!");
            //alert the players that the collector lost the buff and gave the opposite team the points back
            this.dead = false;
        }else {
            game.broadcast(team.getChatColor() + holder + " lost the buff peacefully.");
            //alert the players that the collector lost the buff peacefully
        }
        replaceLine(PREFIX + " " + "INACTIVE");

        this.holder = null;
    }

    /**
     * move to Game
     * @param team
     * @return
     */
    public TeamEnum getOppositeTeam(TeamEnum team) {
        for(GTeam teamL : game.getTeams()) {
            if(teamL.getTeamEnum() == team) continue;
            return teamL.getTeamEnum();
        }
        return null;
    }
}
