package me.raindance.champions.kits.items;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldEvent;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class WaterBottle implements IItem {
    @Override
    public boolean useItem(Player player, Action action) {
        StatusApplier applier = StatusApplier.getOrNew(player);
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            for (Status status : applier.getEffects()) {
                if (status.isNegative()) {
                    if (status.isVanilla()) player.removePotionEffect(status.getVanilla());
                    else applier.removeCustom(status);
                }
            }
        }
        Location location = player.getEyeLocation();
        SoundPlayer.sendSound(location, "random.splash", 0.75F, 88);
        WrapperPlayServerWorldEvent particles = ParticleGenerator.createBlockEffect(location, Material.WATER.getId());
        for(Player p : player.getWorld().getPlayers()) ParticleGenerator.generate(p, particles);
        return true;
    }
}
