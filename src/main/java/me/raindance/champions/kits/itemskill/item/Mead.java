package me.raindance.champions.kits.itemskill.item;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldEvent;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@ItemMetaData(mat = Material.BREAD)
public class Mead implements IItem {
    @Override
    public String getName() {
        return "Mead";
    }

    @Override
    public void useItem(Player player, Action action) {
        eatStew(player);
    }

    private void eatStew(Player player) {
        StatusApplier.getOrNew(player).applyStatus(Status.STRENGTH, 3, 0, true, true);
        Location location = player.getEyeLocation();
        SoundPlayer.sendSound(location, "random.splash", 0.75F, 88);
        WrapperPlayServerWorldEvent red = ParticleGenerator.createBlockEffect(location, Material.BREAD.getId());

        for(Player p : player.getWorld().getPlayers()) ParticleGenerator.generate(p, red);
    }
}
