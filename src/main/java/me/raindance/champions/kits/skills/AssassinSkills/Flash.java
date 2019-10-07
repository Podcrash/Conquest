package me.raindance.champions.kits.skills.AssassinSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.ICharge;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.util.PacketUtil;
import com.podcrash.api.mc.world.BlockUtil;
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
    private int delay;
    private int charges;
    private long lastTimeHit = 0;

    public Flash(Player player, int level) {
        super(player, "Flash", level, SkillType.Assassin, ItemType.AXE, InvType.AXE, -1);
        charges = level + 1;
        MAX_CHARGES = charges;
        this.delay = 5 + level;
        setDesc("Gain a charge every 4 seconds up to a maximum of %%MAX_CHARGES%% charges.",
                "Right click to consume a charge and teleport 7 blocks forwards.",
                "",
                "Its effectiveness is decreased by half after being in combat from the past %%delay%% seconds",
                "You cannot Flash while Slowed.");
        addDescArg("MAX_CHARGES", () -> charges);
        addDescArg("delay", () -> delay);
    }

    @Override
    public void start() {
        if (getPlayer() != null) TimeHandler.repeatedTimeAsync(20L, 0L, this);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    protected void doSkill(PlayerInteractEvent e, Action action) {
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if(StatusApplier.getOrNew(e.getPlayer()).getEffects().contains(Status.SLOW)) return;
        double distance = 34D;
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
                    WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(location.clone().add(0, 1, 0).toVector(), EnumWrappers.Particle.FIREWORKS_SPARK, 2, 0, 0, 0);
                    PacketUtil.syncSend(packet, getPlayers());
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
        if(event.isCancelled()) return;
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
        if(System.currentTimeMillis() - lastTimeHit <= delay * 1000L) return;
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
