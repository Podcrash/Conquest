package me.raindance.champions.kits.items;

import me.raindance.champions.item.ItemManipulationManager;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class Web implements IItem {
    public Web() {

    }

    @Override
    public boolean useItem(Player player, Action action) {
        if(action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return false;
        Location location = player.getLocation();
        Vector vector = location.getDirection();
        vector.normalize().multiply(1.2);
        vector.setY(vector.getY() + 0.15);
        Item item = ItemManipulationManager.intercept(player, Material.WEB, player.getEyeLocation(), vector, (itemm, entity) -> {
            if(entity != null) {
                SoundPlayer.sendSound(player, "random.successful_hit", 0.75F, 126);
            }
            BlockUtil.restoreAfterBreak(itemm.getLocation(), Material.WEB, (byte) 0, 9);
            itemm.remove();
        });
        ItemStack itemStack = item.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Long.toString(System.currentTimeMillis()));
        itemStack.setItemMeta(meta);
        return true;
    }
}
