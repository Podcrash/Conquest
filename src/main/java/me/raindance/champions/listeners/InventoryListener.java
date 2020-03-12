package me.raindance.champions.listeners;

import com.abstractpackets.packetwrapper.WrapperPlayServerSetSlot;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.mc.util.MathUtil;
import me.raindance.champions.Main;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import me.raindance.champions.inventory.*;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryListener extends ListenerBase {
    //TODO: o boi, looks like the day has come where I will be editing this

    public InventoryListener(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Right clicking a beacon in your hand
     */
    @EventHandler
    public void clickBeacon(PlayerInteractEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if(itemStack.getType() == Material.WOOL && GameManager.hasPlayer(event.getPlayer())) {
            if(itemStack.getData() instanceof Wool) {
                event.setCancelled(true);
                Wool woolData = (Wool) itemStack.getData();
                Game game = GameManager.getGame();
                int id = game.getId();
                if(woolData.getColor() == DyeColor.BLUE) {
                    GameManager.joinTeam(event.getPlayer(), TeamEnum.BLUE);
                }else if(woolData.getColor() == DyeColor.RED) {
                    GameManager.joinTeam(event.getPlayer(), TeamEnum.RED);
                }
            }

        }
    }
    /**
     * Right clicking an enchanting table and opening the kit menu
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnchant(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)) {
            e.setCancelled(true);
            Inventory inv = MenuCreator.createGeneralMenu();
            e.getPlayer().openInventory(inv);
        }
    }

    @Deprecated
    private int getClosestSpace(Inventory inventory) {
        if(inventory.getType() != InventoryType.PLAYER) throw new IllegalArgumentException("Only use this method (getClosestSpace(inventory)) for player inventories");
        for(int i = 0; i < 9; i++) {
            if(inventory.getItem(i) == null) return i;
        }
        return -1;
    }

    private boolean isKitSelectMenu(Inventory inv) {
        return !(inv instanceof PlayerInventory) && inv.getName().toLowerCase().contains("menu");
    }
    /**
     *
     * @param inv - apply, edit, delete part
     * @return
     */
    private boolean isBuildMenu(Inventory inv){
        return !(inv instanceof PlayerInventory) && inv.getName().toLowerCase().contains("build");
    }

    /**
     * Actual GUI where you can select skills
     * @param inv
     * @return
     */
    private boolean isClassMenu(Inventory inv) {
        String lower = inv.getName().toLowerCase();
        for (SkillType skillType : SkillType.details()) {
            if (lower.contains(skillType.getName().toLowerCase()) && !lower.contains("build")) {
                return true;
            }
        }
        return false;
    }
    private boolean ownInventory(Player player, Inventory clickedInventory) {
        return player.getInventory() == clickedInventory;
    }

    @EventHandler
    public void clickItem(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack selected = event.getCurrentItem();
        int slot = event.getSlot();
        ClickType clickType = event.getClick();
        boolean cancel = true;
        if(inventory == null || selected == null || selected.getType() == Material.AIR)  {
            event.setCancelled(cancel);
            return;
        }

        if(ownInventory(player, inventory) && !InvFactory.currentlyEditing(player)) return;
        boolean kitMenu = isKitSelectMenu(inventory),
                build = isBuildMenu(inventory),
                classMenu = isClassMenu(inventory),
                ownInv = InvFactory.currentlyEditing(player) && ownInventory(player, inventory);

        cancel = kitMenu || build || classMenu || ownInv;
        if (kitMenu) clickHelmet(player, selected);
        else if(build) buildMenu(player, inventory, selected);
        else if(classMenu) classClickItem(player, inventory, slot, selected, clickType);
        else if(ownInv)
            //if the player is editing his hotbar, don't cancel it.
            if(0 <= slot && slot < 9) cancel = false;
        event.setCancelled(cancel);
    }


    private void clickHelmet(Player p, ItemStack item) {
        if(item.getType() == Material.AIR) return;
        String name = item.getItemMeta().getDisplayName();
        SkillType skillType = SkillType.getByName(name);
        Inventory inv = MenuCreator.createKitTemplate(p, skillType);
        StatusApplier.getOrNew(p).removeStatus(Status.values());
        p.openInventory(inv);
    }

    private void buildMenu(Player clicker, Inventory inventory, ItemStack selected) {
        SkillType skillType = SkillType.getByName(inventory.getName());

        ItemMeta itemMeta = selected.getItemMeta();
        String displayName = itemMeta.getDisplayName();
        String cleanseNumbers = displayName.replaceAll("[^0-9]", "");
        int buildID = Integer.parseInt(cleanseNumbers);
        InvFactory.clickAtBuildMenu(clicker, skillType, selected, buildID);
    }

    /**
     * For clicking items in the menu
     * @param clicker
     * @param inventory
     * @param slot
     * @param selected
     * @param clickType
     */
    private void classClickItem(Player clicker, Inventory inventory, int slot, ItemStack selected, ClickType clickType) {
        if(selected == null || selected.getType() != Material.BOOK) {
            //I forgot the bad sound
            SoundPlayer.sendSound(clicker, "note.pling", .9F, 50);
            return;
        }
        if(clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
            //if the item doesn't have the enchantment, do nothing
            if(!selected.getEnchantments().containsKey(Enchantment.DAMAGE_ALL)) return;
            selected.removeEnchantment(Enchantment.DAMAGE_ALL);
            SoundPlayer.sendSound(clicker, "note.pling", 0.6F, 85);
        }else if(clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT) {
            //if it already has the enchantment, do nothing
            if(selected.getEnchantments().containsKey(Enchantment.DAMAGE_ALL)) return;
            //reduce slot all the way to the factor of 9
            int baseSlot = MathUtil.floor(9, slot);
            //Remove all the enchantments from the books
            for (int end = baseSlot + 9; baseSlot < end; baseSlot++) {
                ItemStack item = inventory.getItem(baseSlot);
                if (item == null || !item.getEnchantments().containsKey(Enchantment.DAMAGE_ALL)) continue;
                item.removeEnchantment(Enchantment.DAMAGE_ALL);
            }
            //enchant the selected item,
            selected.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            SoundPlayer.sendSound(clicker, "note.pling", 0.6F, 126);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onClose(InventoryCloseEvent e) {
        if (!isClassMenu(e.getInventory())) return;
        //assign build
        e.getPlayer().sendMessage("Build would be assigned");

        Inventory inventory = e.getInventory();
        String name = inventory.getName().toLowerCase();
        ChampionsPlayer newPlayer = InvFactory.inventoryToChampion((Player) e.getPlayer(), e.getInventory(), SkillType.getByName(name));

        ChampionsInventory.clearHotbarSelection(newPlayer.getPlayer());
        ChampionsPlayerManager.getInstance().addChampionsPlayer(newPlayer);
        InvFactory.editClose(newPlayer.getPlayer(), newPlayer);
        SoundPlayer.sendSound(newPlayer.getPlayer(), "random.levelup", 0.75F, 63);
    }
}
