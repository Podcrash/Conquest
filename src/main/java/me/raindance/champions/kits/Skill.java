package me.raindance.champions.kits;

import com.podcrash.api.mc.damage.DamageSource;
import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillCooldownEvent;
import com.podcrash.api.mc.game.Game;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.iskilltypes.ISkill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public abstract class Skill implements ISkill, DamageSource {
    protected final Skill instance = this;
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    private final Player player;
    protected float cooldown;
    protected int level;
    private final String name;
    private final SkillType type;
    private final ItemType itype;
    private final InvType invType;
    private boolean boosted;
    private String canUseMessage;

    private final List<String> description;
    protected final Map<String, NumberCallable> mapNumbers;

    private long lastUsed = 0L;
    private boolean valid;
    public Skill(Player player, String name, int level, SkillType type, ItemType itype, InvType invType, float cooldown) {
        this.player = player;
        this.boosted = false;
        this.level = level;
        this.name = name;
        this.cooldown = cooldown;
        this.type = type;
        this.itype = itype;
        this.invType = invType;
        this.description = new ArrayList<>();
        this.mapNumbers = new HashMap<>();
        this.valid = player != null;
        this.canUseMessage = String.format(
                "%sRecharge> %sYou can use %s%s %d%s.",
                ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, name, level, ChatColor.GRAY);

        addDescArg("cooldown", () -> cooldown);
        addDescArg("weight", this::getSkillTokenWeight);
    }

    public int getID() {
        return Objects.hash(name);
    }
    public abstract int getMaxLevel();

    public boolean isInGame() {
        return getChampionsPlayer().isInGame();
    }
    public Game getGame() {
        return getChampionsPlayer().getGame();
    }
    public String getTeam() {
        return (isInGame()) ? getChampionsPlayer().getTeam() : null;
    }

    /**
     * @see ChampionsPlayer#isAlly(Player)
     * If the player is not in a game, just assume that every player is an ally.
     * @param player
     * @return
     */
    public boolean isAlly(LivingEntity player) {
        if(player instanceof Player) {
            return !isInGame() || getChampionsPlayer().isAlly((Player) player);
        }else return false;
    }

    /**
     * Get all the players within a game, if the game exists. If not, get all the players within the world.
     * @return
     */
    public List<Player> getPlayers(){
        return getGame() == null ? getPlayer().getWorld().getPlayers() : getGame().getPlayers();
    }
    /*
    check if person is in water
     */
    protected boolean isInWater() {
        Material m = player.getLocation().getBlock().getType();
        return (m.equals(Material.STATIONARY_WATER) || m.equals(Material.WATER));
    }
    protected boolean isHolding() {
        return getPlayer()
                .getItemInHand()
                .getType()
                .name()
                .toLowerCase()
                .contains(getItype().getName().toLowerCase());
    }

    //getters
    protected String getWaterMessage(){

        return String.format("%sSkill> %sYou cannot use %s%s %sin water.", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, name, ChatColor.GRAY);
    }
    protected String getCooldownMessage() {
        return String.format(
                "%s%s> %s%s %scannot be used for %s%.2f %sseconds",
                ChatColor.BLUE,
                ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer()).getName(),
                ChatColor.GREEN,
                name,
                ChatColor.GRAY,
                ChatColor.GREEN,
                cooldown(),
                ChatColor.GRAY);
    }
    public String getCanUseMessage() {
        return canUseMessage;
    }

    public void setCanUseMessage(String newMessage) {
        this.canUseMessage = newMessage;
    }

    public String getUsedMessage() {
        return String.format(
                "%s%s> %sYou used %s%s %d%s.",
                ChatColor.BLUE, ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer()).getName(), ChatColor.GRAY, ChatColor.GREEN, name, level, ChatColor.GRAY);
    }

    public Player getPlayer() {
        return player;
    }
    public ChampionsPlayer getChampionsPlayer() {
        return ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer());
    }
    public String getName() {
        return this.name;
    }
    public int getLevel() {
        return this.level;
    }
    // By default, this is 1, but for some skills like colo etc
    public int getSkillTokenWeight() {
        return 1;
    }
    public List<String> getDescription() {
        List<String> newDesc = new ArrayList<>();
        newDesc.add("");
        for(final String part : description) {
            String a = part;
            for (String keyWord : mapNumbers.keySet()) {
                a = a.replace(keyWord, ChatColor.YELLOW + mapNumbers.get(keyWord).getNumber().toString() + ChatColor.GRAY);
            }
            newDesc.add(ChatColor.GRAY + a);
        }
        return newDesc;
    }

    public void setDesc(String... desc) {
        if(player == null) {
            this.description.clear();
            Collections.addAll(description, desc);
            description.add("");
            description.add("Skill Token Weight: %%weight%%");
            if(hasCooldown()) description.add("Cooldown: %%cooldown%% seconds.");
        }
    }
    public void setDesc(List<String> desc) {
        if(player == null) {
            this.description.clear();
            description.addAll(desc);
            description.add("");
            description.add("Skill Token Weight: %%weight%%");
            if(hasCooldown()) description.add("Cooldown: %%cooldown%% seconds.");
        }
    }
    public void addDescArg(String arg, NumberCallable callable) {
        if(player == null) this.mapNumbers.put("%%" + arg + "%%", callable);
    }

    public SkillType getType() {
        return type;
    }
    public ItemType getItype() {
        return itype;
    }
    public InvType getInvType() {
        return invType;
    }

    public float getCooldown() {
        return this.cooldown;
    }
    public boolean hasCooldown() {
        return cooldown != -1;
    }
    public boolean onCooldown() {
        return (System.currentTimeMillis() - lastUsed) < cooldown * 1000L;
    }
    public double cooldown() {
        return (double) cooldown - ((double)(System.currentTimeMillis() - lastUsed)) / 1000D;
    }

    public boolean hasEnergy(double energy) {
        if(this instanceof IEnergy) {
            return (getChampionsPlayer().getEnergyBar().getEnergy() - energy  >= 0);
        } else return true;
    }
    public boolean hasEnergy() {
        if(this instanceof IEnergy) {
            return hasEnergy(((IEnergy) this).getEnergyUsage());
        } else return true;
    }
    public String getNoEnergyMessage() {
        return String.format(
                "%sEnergy> %sYou are too exhausted to use %s%s %s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                getName(),
                ChatColor.GRAY
        );
    }

    public long getLastUsed() {
        return lastUsed;
    }
    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
        coolDownEvent();
    }
    public void coolDownEvent() {
        SkillCooldownEvent event = new SkillCooldownEvent(this);
        pluginManager.callEvent(event);
    }

    protected boolean rightClickCheck(Action action) {
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private int boost(int level) {
        int a = boosted ? level + 2 : level;
        if (boosted && a > getBoostedMaxLevel()) {
            a = getBoostedMaxLevel();
        } else if (!boosted && a > getMaxLevel()) a = getMaxLevel();
        return a;
    }
    public boolean isBoosted() {
        return boosted;
    }
    public int getFlatBoostedMaxLevel() {
        return getMaxLevel() + 1;
    }
    public int getBoostedMaxLevel() {
        if (boosted) {
            return getFlatBoostedMaxLevel();
        }
        return getMaxLevel();
    }
    public void setBoosted(boolean isBoosted) {
        this.boosted = isBoosted;
        Main.getInstance().getLogger().info("BEFORE BOOSTED " + level);
        this.level = this.boost(level);
        Main.getInstance().getLogger().info("AFTER BOOSTED " + level);
    }

    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return String.format("%s %s {%s}", this.getClass().getSimpleName(),this.getLevel(), this.player.getName());
    }

    protected interface NumberCallable<T> {
        T getNumber();
    }

    protected interface IntegerCallable extends NumberCallable<Integer> {
        Integer getNumber();
    }

    protected interface FloatCallable extends NumberCallable<Float> {
        Float getNumber();
    }
}
