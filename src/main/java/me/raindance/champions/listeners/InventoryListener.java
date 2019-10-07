package me.raindance.champions.listeners;

import com.abstractpackets.packetwrapper.WrapperPlayServerSetSlot;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryListener extends ListenerBase {
    //TODO: o boi
    private static HashMap<String, Integer> tokenCountMap = new HashMap<>();
    private static HashMap<String, Boolean> currentlyEditing = new HashMap<>();
    private static HashMap<String, Integer> itemTokenCountMap = new HashMap<>();

    public InventoryListener(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Right clicking a beacon in your hand
     */
    @EventHandler
    public void clickBeacon(PlayerInteractEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if(itemStack.getType() == Material.BEACON) {
            event.setCancelled(true);
            event.getPlayer().openInventory(MenuCreator.createGameMenu());
        }else if(itemStack.getType() == Material.WOOL && GameManager.hasPlayer(event.getPlayer())) {
            if(itemStack.getData() instanceof Wool) {
                event.setCancelled(true);
                Wool woolData = (Wool) itemStack.getData();
                Game game = GameManager.getGame();
                int id = game.getId();
                if(woolData.getColor() == DyeColor.BLUE) {
                    GameManager.joinTeam(event.getPlayer(), "blue");
                }else if(woolData.getColor() == DyeColor.RED) {
                    GameManager.joinTeam(event.getPlayer(), "red");
                }
            }

        }
    }
    /**
     * Right clicking an enchanting table
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnchant(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null) {
                if(e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)) {
                    e.setCancelled(true);
                    Inventory inv = MenuCreator.createGeneralMenu();
                    e.getPlayer().openInventory(inv);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpen(InventoryOpenEvent e) {
        if (isClassMenu(e.getInventory())) {
            String name = e.getPlayer().getName();
            Main.getInstance().log.info(name + " is making a kit!");
            currentlyEditing.put(name, true);
            ItemStack goldStack = e.getInventory().getItem(8);
            ItemStack ironStack = e.getPlayer().getInventory().getItem(17);
            int gold = goldStack.getType() == Material.REDSTONE ? 0 : goldStack.getAmount();
            int iron = ironStack.getType() == Material.REDSTONE ? 0 : ironStack.getAmount();
            tokenCountMap.put(name, gold);
            //this is bad code TODO
            int tokenCount = iron;
            itemTokenCountMap.put(name, tokenCount);
        }
    }
    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onClose(InventoryCloseEvent e) {
        currentlyEditing.put(e.getPlayer().getName(), false);
        if (isClassMenu(e.getInventory())) {
            tokenCountMap.remove(e.getPlayer().getName());
            //assign build
            e.getPlayer().sendMessage("Build would be assigned");

            Inventory inventory = e.getInventory();
            String name = inventory.getName().toLowerCase();
            ChampionsPlayer newPlayer = InvFactory.inventoryToChampion((Player) e.getPlayer(), e.getInventory(), SkillType.getByName(name));

            if(newPlayer != null) {
                ChampionsInventory.clearHotbarSelection(newPlayer.getPlayer());
                ChampionsPlayerManager.getInstance().addChampionsPlayer(newPlayer);
                InvFactory.editClose(newPlayer.getPlayer(), newPlayer);
                SoundPlayer.sendSound(newPlayer.getPlayer(), "random.levelup", 0.75F, 63);
            }
        }
    }

    @EventHandler
    public void clickItem(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getWhoClicked() instanceof Player) {
            /*
            Clicking the armor
            */
            if(event.getClickedInventory().getName().contains("Current Champions Games")) {
                ItemStack item = event.getCurrentItem();
                if(item.getItemMeta() == null) return;
                String possNumber = item.getItemMeta().getDisplayName().replaceAll("[^0-9]", "");
                Game game = GameManager.getGame();
                if(game == null) {
                    event.getWhoClicked().sendMessage("Soemthing went wrong");
                    return;
                }
                if(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) game.leftClickAction((Player) event.getWhoClicked());
                if(event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) game.rightClickAction((Player) event.getWhoClicked());
                event.setCancelled(true);
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();
                packet.setSlotData(item);
                packet.setSlot(event.getSlot());
                packet.setWindowId(((CraftPlayer) event.getWhoClicked()).getHandle().activeContainer.windowId);
                PacketUtil.syncSend(packet, (Player) event.getWhoClicked());
            }else if (event.getInventory().getName().toLowerCase().contains("menu")) {
                clickHelmet((Player) event.getWhoClicked(), event.getCurrentItem());
                event.setCancelled(true);
            } else if(event.getInventory().getName().toLowerCase().contains("build")) {
                SkillType skillType = SkillType.getByName(event.getInventory().getName());
                InvFactory.clickAtBuildMenu((Player) event.getWhoClicked(), skillType, event.getCurrentItem());
                event.setCancelled(true);
            }else if(currentlyEditing.get(event.getWhoClicked().getName()) != null && currentlyEditing.get(event.getWhoClicked().getName())) {
                if (isClassMenu(event.getClickedInventory())) {
                    if(event.getCurrentItem().getType() == Material.BOOK) {
                        Player player = (Player) event.getWhoClicked();
                        if(clickItem(event.getClickedInventory(), event.getSlot(), player, event.getCurrentItem(), event.getClick())) {
                            ItemStack goldToken = event.getInventory().getItem(8);
                            if (tokenCountMap.get(player.getName()) <= 0)
                                event.getInventory().setItem(8, new ItemStack(Material.REDSTONE));
                            else if (!event.getInventory().getItem(8).getType().equals(Material.GOLD_INGOT))
                                event.getInventory().setItem(8, new ItemStack(Material.GOLD_INGOT));
                            goldToken.setAmount(tokenCountMap.get(player.getName()));
                        }
                    }
                }else if(event.getClickedInventory() == event.getWhoClicked().getInventory()) {
                    if(event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getType() != Material.IRON_INGOT)
                        clickHotBarItem((Player) event.getWhoClicked(), event.getCurrentItem(), event.getSlot());
                }
                event.setCancelled(true);
            }
        }
    }

    private boolean clickItem(Inventory inventory, int slot, Player player, ItemStack book, ClickType clickType) {
        BookFormatter bf = InventoryData.getSkillFormatter(book);
        if (bf == null) return false;
        ItemStack newBook = book.clone();

        boolean a = false;
        switch (clickType) {
            case LEFT:
                Skill skill = bf.getSkill();
                if (skill != null && tokenCountMap.get(player.getName()) > 0) {
                    boolean b = false;
                    if (newBook.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                        if(newBook.getAmount() >= skill.getMaxLevel())
                            SoundPlayer.sendSound(player, "note.bass", 0.6F, 63);
                        else {
                            newBook.setAmount(newBook.getAmount() + 1);
                            b = true;
                        }
                    } else if (!(newBook.containsEnchantment(Enchantment.DAMAGE_ALL))) {
                        newBook.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                        b = true;
                    }
                    if (b) {
                        SoundPlayer.sendSound(player, "note.pling", 0.6F, 126);
                        tokenCountMap.put(player.getName(), tokenCountMap.get(player.getName()) - bf.getSkillTokenWeight());
                        a = true;
                    }

                }
                break;
            case RIGHT:
                if (!newBook.containsEnchantment(Enchantment.DAMAGE_ALL)) break;
                else if (newBook.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                    if (newBook.getAmount() > 1) {
                        newBook.setAmount(newBook.getAmount() - 1);
                    } else {
                        newBook.setAmount(1);
                        newBook.removeEnchantment(Enchantment.DAMAGE_ALL);
                    }
                    a = true;
                    tokenCountMap.put(player.getName(), tokenCountMap.get(player.getName()) + bf.getSkillTokenWeight());
                    SoundPlayer.sendSound(player, "note.pling", 0.6F, 95);
                }
                break;
        }
        if(a) {
            int display = (!newBook.containsEnchantment(Enchantment.DAMAGE_ALL)) ? newBook.getAmount() - 1 : newBook.getAmount();
            ItemMeta meta = newBook.getItemMeta();
            meta.setDisplayName(bf.getHeader(display));
            meta.setLore(bf.getDescription(newBook.getAmount() - 1));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            newBook.setItemMeta(meta);
            inventory.setItem(slot, newBook);
            ItemStack[] contents = inventory.getContents();
            inventory.clear();
            inventory.setContents(contents);
        }
        return a;

    }

    private void clickHotBarItem(Player player, ItemStack itemS, int slotID) {
        Inventory inventory = player.getInventory();
        String name = player.getName();
        int currentItemTokens = itemTokenCountMap.get(name);
        ChampionsItem item = ChampionsItem.getByName(itemS.getItemMeta().getDisplayName());
        int closestSpace = getClosestSpace(inventory);
        if(item == null) {
            player.sendMessage("It wasn't supposed to be null, is this a bug?");
            return;
        }
        if(0 <= slotID && slotID < 9) {
            inventory.setItem(slotID, new ItemStack(Material.AIR));
            itemTokenCountMap.put(name, currentItemTokens + item.getTokenCost());
        }else if(slotID < 36 && closestSpace != -1) {
            if(currentItemTokens == 0) return;
            int diff = currentItemTokens - item.getTokenCost();
            if(diff < 0) return;
            itemTokenCountMap.put(name, diff);
            inventory.setItem(closestSpace, item.toItemStack());

        }
        //update
        ItemStack iron = (itemTokenCountMap.get(name) != 0) ? new ItemStack(Material.IRON_INGOT, itemTokenCountMap.get(name)) : new ItemStack(Material.REDSTONE);
        inventory.setItem(17, iron);
        SoundPlayer.sendSound(player, "note.pling", 0.6F, 126);
        player.updateInventory();
    }

    private int getClosestSpace(Inventory inventory) {
        if(inventory.getType() != InventoryType.PLAYER) throw new IllegalArgumentException("Only use this method (isThereSpace(inventory)) for player inventories");
        for(int i = 0; i < 9; i++) {
            if(inventory.getItem(i) == null) return i;
        }
        return -1;
    }

    private void clickHelmet(Player p, ItemStack item) {
        if(item.getType() == Material.AIR) return;
        Inventory inv = MenuCreator.createKitTemplate(p, findSkillType(item));
        p.openInventory(inv);
    }

    private SkillType findSkillType(ItemStack item) {
        SkillType skillType;
        switch (item.getType()) {
            case LEATHER_HELMET:
                skillType = SkillType.Assassin;
                break;
            case CHAINMAIL_HELMET:
                skillType = SkillType.Ranger;
                break;
            case GOLD_HELMET:
                skillType = SkillType.Mage;
                break;
            case IRON_HELMET:
                skillType = SkillType.Knight;
                break;
            case DIAMOND_HELMET:
                skillType = SkillType.Brute;
                break;
            default:
                throw new IllegalArgumentException(item.getType() + " is not proper!");
        }
        return skillType;
    }

    private boolean isClassMenu(Inventory inv) {
        final List<String> strs = Arrays.asList("assassin", "brute", "knight", "ranger", "mage");
        String lower = inv.getName().toLowerCase();
        for (int i = 0; i < 5; i++) {
            if (lower.contains(strs.get(i)) && !lower.contains("build")) {
                return true;
            }
        }
        return false;
    }
}
