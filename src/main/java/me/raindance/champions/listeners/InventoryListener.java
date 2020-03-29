package me.raindance.champions.listeners;

import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.ChampionsKitTable;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.mc.util.MathUtil;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import me.raindance.champions.inventory.*;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class InventoryListener extends ListenerBase {
    public static boolean lock = true;
    //TODO: o boi, looks like the day has come where I will be editing this

    public InventoryListener(JavaPlugin plugin) {
        super(plugin);
    }
    private boolean invincible = false;

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
            openGeneralMenu(e.getPlayer());
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

    private boolean isInvincibleMenu(Inventory inv) {
        return isKitSelectMenu(inv) || isBuildMenu(inv) || isClassMenu(inv) || isConfirmationMenu(inv);
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
            if (lower.contains(skillType.getName().toLowerCase()) && !lower.contains("build") && !skillType.equals(SkillType.Global)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param inv
     * @return whether the inventory is a confirmation menu for a skill purchase
     */
    private boolean isConfirmationMenu(Inventory inv) {
        return !(inv instanceof PlayerInventory) && inv.getName().toLowerCase().contains("purchasing");
    }

    /**
     *
     * @param player
     * @param inventory
     * @return whether the inventory is the player's inventory in a lobby
     */
    private boolean isLobbyMenu(Player player, Inventory inventory) {
        return ownInventory(player, inventory) && !InvFactory.currentlyEditing(player);
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

        if(event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT || inventory == null || selected == null)  {
            event.setCancelled(true);
            return;
        }

        boolean lobbyMenu = isLobbyMenu(player, inventory),
                kitMenu = isKitSelectMenu(inventory),
                build = isBuildMenu(inventory),
                classMenu = isClassMenu(inventory),
                confirmationMenu = isConfirmationMenu(inventory),
                ownMenuEditing = InvFactory.currentlyEditing(player) && ownInventory(player, inventory);

        cancel = lobbyMenu || kitMenu || build || classMenu || confirmationMenu || ownMenuEditing;
        if (lobbyMenu) lobbyMenuEdit(player, inventory, selected);
        else if (kitMenu) clickHelmet(player, selected);
        else if(build) buildMenu(player, inventory, selected);
        else if(classMenu) classClickItem(player, inventory, slot, selected, clickType);
        else if(confirmationMenu) confirm(player, inventory, slot);
        else if(ownMenuEditing)
            //if the player is editing his hotbar, don't cancel it.
            if(0 <= slot && slot < 9) cancel = false;
        event.setCancelled(cancel);
    }

    private void openGeneralMenu(Player player) {
        if(GameManager.isSpectating(player)) {
            player.sendMessage(String.format(
                    "%sConquest> %sYou may not select a class while spectating.",
                    ChatColor.BLUE,
                    ChatColor.GRAY));
            return;
        }
        Inventory inv = MenuCreator.createGeneralMenu();
        player.openInventory(inv);
    }

    private void lobbyMenuEdit(Player player, Inventory inventory, ItemStack selected) {
        if (selected.getItemMeta() == null) return;
        String name = selected.getItemMeta().getDisplayName().toLowerCase();
        if (name.contains("select")) {
            if (name.contains("kit")) {
                openGeneralMenu(player);
            } else if (name.contains("team")) {
                if (GameManager.isSpectating(player)) {
                    player.sendMessage(
                            String.format(
                                    "%sInvicta> %sYou are spectating %sGame %s%s.",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY,
                                    ChatColor.GREEN,
                                    GameManager.getGame().getId(),
                                    ChatColor.GRAY));
                    return;
                }
                inventory.clear();
                MenuCreator.openTeamSelectMenu(player);
            }
        } else if (name.contains("queue")) {
            //TODO: this should leave the queue for a team, but currently, teams are forced on join i.e you can't not be in a queue

        } else if (name.contains("spectator")) {
            GameManager.getGame().toggleSpec(player);
        } else if (name.contains("red team")) {
            GameManager.joinTeam(player, TeamEnum.RED);
            GameManager.getGame().updateLobbyInventory(player);
        } else if (name.contains("blue team")) {
            GameManager.joinTeam(player, TeamEnum.BLUE);
            GameManager.getGame().updateLobbyInventory(player);
        }
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
        if(selected.getItemMeta() == null) return;
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
        if(selected.getType() == Material.BOOK) handleSkillTokens(clicker, inventory, slot, selected, clickType);
        else if (selected.getType() == Material.PAPER) attemptBuy(clicker, inventory, slot, selected, clickType);
        else SoundPlayer.sendSound(clicker, "note.pling", .9F, 50);
    }

    private void attemptBuy(Player clicker, Inventory inventory, int slot, ItemStack selected, ClickType clickType) {
        EconomyHandler handler = (EconomyHandler) Pluginizer.getSpigotPlugin().getEconomyHandler();

        handler.buy(clicker, selected.getItemMeta().getDisplayName()).exceptionally(t -> {
            DBUtils.handleThrowables(t);
            return null;
        });
    }

    /**
     *
     * @param clicker - The player
     * @param inventory - The inventory
     * @param slot - The slot (11 = confirm) (15 = cancel)
     */
    private void confirm(Player clicker, Inventory inventory, int slot) {
        EconomyHandler handler = (EconomyHandler) Pluginizer.getSpigotPlugin().getEconomyHandler();
        if (slot == 11) {
            handler.confirm(clicker, ChatUtil.strip(inventory.getItem(13).getItemMeta().getDisplayName()));
        } else if (slot == 15) {
            handler.cancel(clicker, ChatUtil.strip(inventory.getItem(13).getItemMeta().getDisplayName()));
        }
    }

    private void handleSkillTokens(Player clicker, Inventory inventory, int slot, ItemStack selected, ClickType clickType) {
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

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (isInvincibleMenu(e.getInventory())) DamageApplier.addInvincibleEntity(e.getPlayer());

        if(lock) return;
        if(!isClassMenu(e.getInventory())) return;
        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        CompletableFuture<Set<String>> future = table.getAllowedSkillsFuture(e.getPlayer().getUniqueId());
        future.thenAccept(skillSet -> {
            for(ItemStack item : e.getInventory()) {
                if(item == null || item.getType() != Material.BOOK) continue;
                String name = ChatUtil.strip(item.getItemMeta().getDisplayName());
                if(skillSet.contains(name)) continue;
                item.setType(Material.PAPER);
            }
        }).exceptionally(throwable -> {
            DBUtils.handleThrowables(throwable);
            return null;
        });
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onClose(InventoryCloseEvent e) {
        if (isInvincibleMenu(e.getInventory())) DamageApplier.removeInvincibleEntity(e.getPlayer());
        if (!isClassMenu(e.getInventory())) {
            if (!GameManager.getGame().isOngoing()
                    && (e.getPlayer() instanceof Player)
                    && !isInvincibleMenu(e.getInventory())) {
                GameManager.getGame().updateLobbyInventory((Player) e.getPlayer());
            }
            return;
        }
        //assign build
        Inventory inventory = e.getInventory();
        String name = inventory.getName().toLowerCase();
        ChampionsPlayer newPlayer = InvFactory.inventoryToChampion((Player) e.getPlayer(), e.getInventory(), SkillType.getByName(name));

        ChampionsInventory.clearHotbarSelection(newPlayer.getPlayer());
        ChampionsPlayerManager.getInstance().addChampionsPlayer(newPlayer);
        InvFactory.editClose(newPlayer.getPlayer(), newPlayer);
        if(!GameManager.getGame().isOngoing()) {
            GameManager.getGame().updateLobbyInventory(newPlayer.getPlayer());
        }
        SoundPlayer.sendSound(newPlayer.getPlayer(), "random.levelup", 0.75F, 63);
    }
}
