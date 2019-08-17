package me.raindance.champions.kits.skills.AssassinSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.ICharge;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.world.BlockUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Flash extends Instant implements ICharge, IPassiveTimer {
    //rate of charges still not implemented
    private final int MAX_CHARGES;
    private final int MAX_LEVEL = 4;
    private long lastTimeHit = 0;
    private int charges;

    public Flash(Player player, int level) {
        super(player, "Flash", level, SkillType.Assassin, ItemType.AXE, InvType.AXE, -1);
        charges = level + 1;
        MAX_CHARGES = charges;
        setDesc("Gain a charge every 4 seconds up to a maximum of %%MAX_CHARGES%% charges.",
                "Right click to consume a charge and teleport 7 blocks forwards.",
                "",
                "Its effectiveness is decreased by half after being in combat from the past 6 seconds",
                "You cannot Flash while Slowed.");
        addDescArg("MAX_CHARGES", () -> charges);
    }

    @Override
    public void start() {
        if (getPlayer() != null) TimeHandler.repeatedTimeSeconds(1L, 0L, this);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    protected void doSkill(PlayerInteractEvent e, Action action) {
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        double distance = 35D;
        if(System.currentTimeMillis() - lastTimeHit <= 6000L) distance /= 2D;
        Player player = getPlayer();

        //if player still has charges, then use the skill
        if (getCurrentCharges() > 0) {
            Location location = player.getLocation().add(new Vector(0, 0.1, 0));
            Vector increment = player.getLocation().getDirection().normalize().multiply(0.2);
            for (int i = 0; i < distance; i++) {
                if (!BlockUtil.isSafe(location)) {
                    location.subtract(increment.multiply(2));
                    break;
                } else {
                    WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(location.clone().add(0, 1, 0), EnumWrappers.Particle.FIREWORKS_SPARK, 2, 0, 0, 0);
                    getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
                    location.add(increment);
                }
            }
            player.teleport(location);
            player.setFallDistance(0);
            player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 0.4f, 1.2f);
            player.getWorld().playSound(player.getLocation(), Sound.SILVERFISH_KILL, 1f, 1.6f);
            charges--;
            player.sendMessage(String.format("%sSkill> %sFlash Charges: %s%d",
                    ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getCurrentCharges()));
        } else {
        //if the player is out of charges, don't let them use the skill
            player.sendMessage(String.format("%sSkill> %sNo flash charges left.",
                    ChatColor.BLUE, ChatColor.GRAY));
        }
    }

    @EventHandler
    public void hit(DamageApplyEvent event) {
        if(event.getVictim() == getPlayer()) {
            lastTimeHit = System.currentTimeMillis();
        }
    }

    public void task() {
        addCharge();
    }

    public boolean cancel() {
        return false;
    }

    public void cleanup() {
        charges = 0;
    }

    public void addCharge() {
        if (getCurrentCharges() < MAX_CHARGES && System.currentTimeMillis() - getLastUsed() >= 4000L) {
            charges++;
            this.getPlayer().sendMessage(String.format("%sSkill> %sFlash Charges: %s%d",
                    ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getCurrentCharges()));
            setLastUsed(System.currentTimeMillis());
        }
    }

    public int getCurrentCharges() {
        return charges;
    }

    public int getMaxCharges() {
        return MAX_CHARGES;
    }
}
