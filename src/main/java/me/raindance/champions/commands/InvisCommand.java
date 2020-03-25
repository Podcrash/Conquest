package me.raindance.champions.commands;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class InvisCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.developer")) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation();

            float[] data = new float[]{
                Float.parseFloat(args[0]),
                    Float.parseFloat(args[1]),
                    Float.parseFloat(args[2]),
            };

            float particleData = Float.parseFloat(args[3]);
            WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(playerLocation.toVector(),
                    EnumWrappers.Particle.REDSTONE, new int[]{}, 0,
                    data[0], data[1], data[2]);

            particle.setParticleData(particleData);

            PacketUtil.asyncSend(particle, playerLocation.getWorld().getPlayers());
        } else {
            sender.sendMessage(String.format("%sChampions> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}