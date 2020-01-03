package me.raindance.champions.kits;

import com.podcrash.api.mc.damage.DamageSource;
import com.podcrash.api.mc.game.TeamEnum;
import me.raindance.champions.events.skill.SkillCooldownEvent;
import com.podcrash.api.mc.game.Game;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.iskilltypes.champion.ISkill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;

import java.util.*;

@SkillMetadata
public abstract class Skill implements ISkill, DamageSource {
    private String playerName;
    protected final Skill instance = this;
    private long lastUsed = 0L;

    public Skill() {

    }

    /**
     * OVERRIDE THIS
     */
    public void init() {

    }

    public int getID() {
        return Objects.hash(getName());
    }

    public boolean isInGame() {
        return getChampionsPlayer().isInGame();
    }
    public Game getGame() {
        return getChampionsPlayer().getGame();
    }
    public TeamEnum getTeam() {
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
        return getGame() == null ? getPlayer().getWorld().getPlayers() : getGame().getBukkitPlayers();
    }
    /*
    check if person is in water
     */
    protected boolean isInWater() {
        Material m = getPlayer().getLocation().getBlock().getType();
        return (m.equals(Material.STATIONARY_WATER) || m.equals(Material.WATER));
    }
    protected boolean isHolding() {
        String name = getItemType().getName();
        String upperCasedItemName = getPlayer().getItemInHand().getType().name().toUpperCase();
        return (name == null) || upperCasedItemName.contains(name);
    }

    //getters
    protected String getWaterMessage(){

        return String.format("%sSkill> %sYou cannot use %s%s %sin water.", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }

    public String getUsedMessage() {
        return String.format("%s%s> %sYou used %s%s%s.",
                ChatColor.BLUE, ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer()).getName(), ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }
    public String getUsedMessage(LivingEntity entity) {
        return String.format("%sSkill> %sYou used %s%s %son %s%s.",
                ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY, ChatColor.YELLOW, entity.getName());
    }

    //TODO: change verb to something else
    public String getDurationMessage(LivingEntity entity, String verb, double duration) {
        return String.format("%s%s> %sYou %s %s%s %sfor %s%f.",
                ChatColor.BLUE, getName(), ChatColor.GRAY, verb, ChatColor.YELLOW, entity.getName(), ChatColor.GRAY, ChatColor.GREEN, duration);
    }

    public String getMustGroundMessage() {
        return String.format("%s%s> %sYou cannot use %s%s%s while grounded.",
                ChatColor.BLUE, getChampionsPlayer().getName() , ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerName);
    }
    public ChampionsPlayer getChampionsPlayer() {
        return ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer());
    }
    public void setPlayer(Player player) {
        this.playerName = player.getName();
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
        Bukkit.getPluginManager().callEvent(event);
    }

    protected boolean rightClickCheck(Action action) {
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public String toString() {
        return "{Skill " +
                getName() + " " +
                getItemType() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(getID(), skill.getID());
    }

    @Override
    public int hashCode() {
        return getID();
    }
}
