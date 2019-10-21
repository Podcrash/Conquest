package me.raindance.champions.kits.items;

import me.raindance.champions.Main;
import com.podcrash.api.mc.listeners.ListenerBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/*
    This dude manages items
 */
public class ItemHelper extends ListenerBase {
    public ItemHelper(JavaPlugin plugin) {
        super(plugin);
    }

    private static final HashMap<Material, IItem> map = new HashMap<>();

    static {
        map.put(Material.MUSHROOM_SOUP, new Soup());
        map.put(Material.POTION, new WaterBottle());
        map.put(Material.WEB, new Web());
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void click(PlayerInteractEvent e) {
        if(e.getItem() == null) return;
        IItem itemHandler = map.getOrDefault(e.getItem().getType(), null);
        if (itemHandler != null) {
            if(itemHandler.useItem(e.getPlayer(), e.getAction())) {
                removeItemFromHand(e.getPlayer());
            }
        }
    }

    private void removeItemFromHand(Player player) {
        ItemStack item = player.getItemInHand();
        int slot = player.getInventory().getHeldItemSlot();
        int amnt = item.getAmount();
        if(amnt > 1) {
            item.setAmount(amnt - 1);
        }else {
            PlayerInventory inventory = player.getInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> inventory.clear(slot), 1);
        }
        player.updateInventory();
    }
}
