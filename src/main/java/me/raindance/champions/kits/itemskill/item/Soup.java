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

import java.util.Random;

@ItemMetaData(mat = Material.MUSHROOM_SOUP)
public class Soup implements IItem {
    @Override
    public String getName() {
        return "Mushroom Soup";
    }

    @Override
    public void useItem(Player player, Action action) {
        eatStew(player);
    }

    private void eatStew(Player player) {
        StatusApplier.getOrNew(player).applyStatus(Status.REGENERATION, 4, 1, true, true);
        Location location = player.getEyeLocation();
        SoundPlayer.sendSound(location, "random.eat", 0.85F, 63, null);
        WrapperPlayServerWorldEvent red = ParticleGenerator.createBlockEffect(location, Material.RED_MUSHROOM.getId());
        WrapperPlayServerWorldEvent brown = ParticleGenerator.createBlockEffect(location, Material.BROWN_MUSHROOM.getId());

        for(Player p : player.getWorld().getPlayers()) ParticleGenerator.generate(p, red, brown);
    }
}
