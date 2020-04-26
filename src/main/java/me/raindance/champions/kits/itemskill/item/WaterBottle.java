package me.raindance.champions.kits.itemskill.item;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldEvent;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@ItemMetaData(mat = Material.POTION, data = 0)
public class WaterBottle implements IItem {
    @Override
    public String getName() {
        return "Water Bottle";
    }

    @Override
    public void useItem(Player player, Action action) {
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
    }
}
