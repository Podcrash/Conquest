package me.raindance.champions.kits.itemskill.item;

import com.packetwrapper.abstractpackets.WrapperPlayServerEntityStatus;
import com.packetwrapper.abstractpackets.WrapperPlayServerWorldEvent;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@ItemMetaData(mat = Material.BREAD, actions = {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK})
public class Mead implements IItem {
    @Override
    public String getName() {
        return "Mead";
    }

    @Override
    public void useItem(Player player, Action action) {
        eatBread(player);
    }

    private void eatBread(Player player) {
        StatusApplier.getOrNew(player).applyStatus(Status.STRENGTH, 3, 0, true, true);
        Location location = player.getEyeLocation();
        SoundPlayer.sendSound(location, "random.eat", 0.75F, 88);
        WrapperPlayServerWorldEvent eat = ParticleGenerator.createBlockEffect(location, Material.BREAD.getId());

        WrapperPlayServerEntityStatus status = new WrapperPlayServerEntityStatus();
        status.setEntityId(WrapperPlayServerEntityStatus.Status.EATING_ACCEPTED);
        status.setEntityId(player.getEntityId());

        for(Player p : player.getWorld().getPlayers())
            ParticleGenerator.generate(p, status, eat);
    }
}
