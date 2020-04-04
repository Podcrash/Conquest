package me.raindance.champions.kits.itemskill.item;

import com.podcrash.api.mc.events.ItemCollideEvent;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@ItemMetaData(mat = Material.WEB, actions = {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK})
public class Web implements IItem, Listener {
    private final Map<Integer, String> itemIDs = new HashMap<>();

    @Override
    public String getName() {
        return "Cobweb";
    }

    @Override
    public void useItem(Player player, Action action) {
        Location location = player.getLocation();
        Vector vector = throwVector(location.getDirection());
        Item spawnItem = ItemManipulationManager.regular(Material.WEB, player.getEyeLocation(), vector);
        itemIDs.put(spawnItem.getEntityId(), player.getName());
        Item item = ItemManipulationManager.intercept(spawnItem, 1.1, (itemm, entity, land) -> {
            if(entity != null) {
                SoundPlayer.sendSound(player, "random.successful_hit", 0.75F, 126);
            }
            if(itemm.getLocation().getBlock().getType() != Material.WEB)
                BlockUtil.restoreAfterBreak(itemm.getLocation(), Material.WEB, (byte) 0, 9);
            itemm.remove();
        });
        ItemStack itemStack = item.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Long.toString(System.currentTimeMillis()));
        itemStack.setItemMeta(meta);
    }

    @EventHandler
    public void collideItem(ItemCollideEvent e) {
        if(e.isCancelled()) return;
        //identity check + owner of item check = cancel collision
        String ownerName = itemIDs.get(e.getItem().getEntityId());
        if(ownerName == null) return;
        if(!ownerName.equalsIgnoreCase(e.getCollisionVictim().getName())) return;
        e.setCancelled(true);
    }
}
