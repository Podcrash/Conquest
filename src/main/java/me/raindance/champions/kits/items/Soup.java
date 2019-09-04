package me.raindance.champions.kits.items;

import com.comphenix.packetwrapper.WrapperPlayServerWorldEvent;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Random;

public class Soup implements IItem {
    private final Random rand = new Random();

    @Override
    public boolean useItem(Player player, Action action) {
        StatusApplier.getOrNew(player).applyStatus(Status.REGENERATION, 4, 1, true, true);
        Location location = player.getEyeLocation();
        SoundPlayer.sendSound(location, "random.eat", 0.85F, 63, null);
        WrapperPlayServerWorldEvent red = ParticleGenerator.createBlockEffect(location, Material.RED_MUSHROOM.getId());
        WrapperPlayServerWorldEvent brown = ParticleGenerator.createBlockEffect(location, Material.BROWN_MUSHROOM.getId());

        for(Player p : player.getWorld().getPlayers()) ParticleGenerator.generate(p, red, brown);
        return true;
    }
}
