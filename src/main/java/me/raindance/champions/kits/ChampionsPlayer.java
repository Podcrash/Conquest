package me.raindance.champions.kits;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.sound.SoundWrapper;
import me.raindance.champions.inventory.ChampionsInventory;
import me.raindance.champions.inventory.ChampionsItem;
import me.raindance.champions.kits.classes.Druid;
import me.raindance.champions.kits.classes.Sorcerer;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ChampionsPlayer {
    private Player player;
    private double fallDamage = 0;
    private SoundWrapper sound; // sound when hit
    private EnergyBar ebar = null;
    private JsonObject jsonObject;

    protected List<Skill> skills = null;
    private ItemStack[] defaultHotbar;
    protected Material[] armor;

    public ChampionsPlayer(Player player) {
        this.player = player;
        getDefaultHotbar();
    }

    public abstract SkillType getType();
    public abstract int getHP();

    public boolean equip(){
        if(armor[0] == null) return false;
        ItemStack[] armors = new ItemStack[4];
        for(int i = 0; i < armor.length; i++){
            Material mat = armor[i];
            if(mat == null) continue;
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(mat));
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);
            nmsStack.setTag(tag);

            armors[i] = new ItemStack(CraftItemStack.asBukkitCopy(nmsStack));
        }
        player.getEquipment().setArmorContents(armors);
        return true;
    }
    public void resetCooldowns() {
        for(Skill skill : skills) {
            skill.setLastUsedDirect(0);
            if(skill instanceof IConstruct)
                ((IConstruct) skill).afterRespawn();
        }
    }

    public String getName() {
        return getType().getName();
    }

    public boolean isInGame() {
        return GameManager.hasPlayer(this.player);
    }
    public Game getGame() {
        return GameManager.getGame();
    }
    public TeamEnum getTeam() {
        if (isInGame()) return GameManager.getGame().getTeamEnum(getPlayer());
        else return null;
    }

    /**
     * Check if the player is allied with the player.
     * Both players must be in a game.
     * @param player the player to check
     * @return true/false
     */
    public boolean isAlly(Player player) {
        return getGame().isOnSameTeam(getPlayer(), player);
    }

    public void respawn(){//TODO: Respawn with the hotbar.
        player.setFallDistance(0);
        StatusApplier.getOrNew(player).removeStatus(Status.values());
        getInventory().clear();
        this.restockInventory();
        this.resetCooldowns();
        player.setAllowFlight(false);
        player.setFlying(false);
        List<Player> players = getGame() == null ? player.getWorld().getPlayers() : getGame().getBukkitPlayers();
        for(Player player : players){
            if(player != getPlayer()) player.showPlayer(getPlayer());
        }
        player.setHealth(player.getMaxHealth());
        if(this instanceof Druid || this instanceof Sorcerer) {
            ebar.setEnergy(ebar.getMaxEnergy());
        }

        //StatusApplier.getOrNew(player).removeStatus(Status.INEPTITUDE);
    }

    /**
     * TODO: make this method null
     * This method should be overridden to set up special effects that classes need.
     */
    public void effects() {}

    public void skillsRead() {
        player.sendMessage(ChatColor.YELLOW + this.getName());
        for(Skill skill : skills) {
            IChatBaseComponent message = IChatBaseComponent.ChatSerializer.a(
                    "{\"text\":\"" + String.format("%s%s: ", ChatColor.GREEN, SkillInfo.getSkill(SkillInfo.getSkillID(skill)).getInvType().getName() ) + "\"," +
                        "\"extra\":[{\"text\":\"" + String.format("%s%s", ChatColor.WHITE, skill.getName()) + "\"," +
                        "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + niceLookingDescription(skill) + "\"}}]}");
            PacketPlayOutChat packet = new PacketPlayOutChat(message, (byte) 1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private String niceLookingDescription(Skill skill) {
        StringBuilder result = new StringBuilder();
        List<String> description = SkillInfo.getSkillData(skill).getDescription();
        for(int i = 0; i < description.size(); i++) {
            String line = description.get(i);
            if(line != null) {
                result.append(line);
            }
            if(i != description.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    public void heal(double health){
        Player player = getPlayer();

        //call event
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(player, health, EntityRegainHealthEvent.RegainReason.CUSTOM);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        //heal player
        double current = player.getHealth();
        double expected = current + health;
        if(expected >= player.getMaxHealth()){
            player.setHealth(player.getMaxHealth());
        }else player.setHealth(expected);
    }

    public Player getPlayer() {
        return player;
    }
    public CraftPlayer getCraftPlayer() {
        return (CraftPlayer) player;
    }
    public EntityPlayer getEntityCraftPlayer() {
        return this.getCraftPlayer().getHandle();
    }

    public ItemStack[] getArmor() {
        return this.player.getEquipment().getArmorContents();
    }
    public double getArmorValue(double halfHearts) {
        return (halfHearts - getHP())/(-0.04*getHP());
    }
    public double getArmorValue() {
        return getArmorValue(20);
    }
    public Inventory getInventory() {
        return this.player.getInventory();
    }

    public ItemStack[] getDefaultHotbar() {
        if(this.defaultHotbar == null) {
            this.defaultHotbar = new ItemStack[] {
                    new ItemStack(ChampionsItem.DUELIST_SWORD.toItemStack()),
                    new ItemStack(ChampionsItem.MUSHROOM_STEW.toItemStack()),
                    new ItemStack(ChampionsItem.MUSHROOM_STEW.toItemStack()),
                    new ItemStack(ChampionsItem.MUSHROOM_STEW.toItemStack()),
                    new ItemStack(ChampionsItem.MUSHROOM_STEW.toItemStack()),
            };
        }
        return this.defaultHotbar;
    }
    public void setDefaultHotbar(ItemStack[] items) {
        this.defaultHotbar = items;
    }
    public void setDefaultHotbar() {
        Inventory inventory = player.getInventory();
        ItemStack[] hotbar = new ItemStack[9];
        for(int i = 0; i < 9; i++) {
            ItemStack item;
            if((item = inventory.getItem(i)) != null) hotbar[i] = item.clone();
        }
        setDefaultHotbar(hotbar);
    }
    public ItemStack[] getHotBar() {
        ItemStack[] hotbar = new ItemStack[9];
        for (int i = 0; i <= 8; i++) {
            hotbar[i] = this.getInventory().getItem(i);
        }
        return hotbar;
    }
    public boolean hotBarContains(Material material) {
        for(ItemStack item : getDefaultHotbar()) {
            if(item != null && item.getType() == material)
                return true;
        }
        return false;
    }

    private ItemStack getTNTStack(){
        int amount = 0;
        for(ItemStack content : getInventory().getContents()){
            if(content != null && content.getType().equals(Material.TNT)) amount += content.getAmount();
        }
        if(amount == 0) return null;
        return new ItemStack(Material.TNT, amount);
    }

    public void restockInventory() {
        int size = this.defaultHotbar.length;
        ItemStack TNT = getTNTStack();
        ChampionsInventory.clearHotbarSelection(player);
        int i = 0;
        for (; i < size; i++) {
            ItemStack item = this.defaultHotbar[i];
            if(item != null) this.getInventory().setItem(i, item.clone());
            else this.getInventory().setItem(i, null);
        }
        if(TNT != null) getInventory().addItem(TNT.clone());
        this.equip();
    }

    public double getFallDamage() {
        return fallDamage;
    }
    public void setFallDamage(double fallDamage) {
        this.fallDamage = fallDamage;
    }

    public void setUsesEnergy(boolean usesEnergy){
        setUsesEnergy(usesEnergy, 180);
    }
    public void setUsesEnergy(boolean usesEnergy, double maxEnergy){
        if(usesEnergy){
            ebar = new EnergyBar(player, maxEnergy);
        } else {
            if(ebar == null) return;
            ebar.unregister();
            ebar.stop();
            ebar = null;
        }
    }
    public EnergyBar getEnergyBar(){
        return ebar;
    }

    public SoundWrapper getSound() {
        return sound;
    }
    public void setSound(SoundWrapper sound) {
        this.sound = sound;
    }

    public boolean isCloaked() {
        return StatusApplier.getOrNew(this.player).isCloaked();
    }
    public boolean isMarked() {
        return StatusApplier.getOrNew(this.player).isMarked();
    }
    public boolean isSilenced() {
        return StatusApplier.getOrNew(this.player).isSilenced();
    }
    public boolean isShocked() {
        return StatusApplier.getOrNew(this.player).isShocked();
    }

    public List<Skill> getSkills() {
        return skills;
    }
    public Skill getCurrentSkillInHand() {
        final Material material = player.getItemInHand().getType();
        for(Skill skill : skills) {
            if(skill.getItemType() == null || skill.getItemType() == ItemType.NULL) continue;
            String name = skill.getItemType().getName();
            if(name != null && material.name().contains(name)) return skill;
        }
        return null;
    }

    public JsonObject serialize() {
        if(jsonObject != null) return jsonObject;
        JsonObject championsObject = new JsonObject();

        championsObject.addProperty("skilltype", this.getType().getName().toLowerCase());

        JsonArray skillArray = new JsonArray();

        for(Skill skill : skills)
            skillArray.add(SkillInfo.getSkillID(skill));

        JsonObject itemsSerial = new JsonObject();
        for(int i = 0; i < defaultHotbar.length; i++) {
            ItemStack item = defaultHotbar[i];
            if(item == null || item.getType() == Material.AIR) continue;
            ChampionsItem championsItem = ChampionsItem.getByName(item.getItemMeta().getDisplayName());
            int slotID = (championsItem == null) ? -1 : championsItem.getSlotID();
            itemsSerial.addProperty(Integer.toString(i), slotID);
        }

        championsObject.add("skills", skillArray);
        championsObject.add("items", itemsSerial);
        this.jsonObject = championsObject;
        return championsObject;
    }
}

