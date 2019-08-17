package me.raindance.champions.events;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.damage.Damage;
import me.raindance.champions.kits.Skill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Only for players
 */
public class DeathApplyEvent extends Event implements Cancellable {
    //this is cancellable because maybe cancel death later??
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private LivingEntity attacker;
    private Damage damage;
    private boolean cancel;


    public DeathApplyEvent(Damage lastDamage) {
        this.player = (Player) lastDamage.getVictim();
        this.attacker = lastDamage.getAttacker();
        this.damage = lastDamage;
    }

    public Player getPlayer() {
        return player;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage.getDamage();
    }

    public Cause getCause() {
        return damage.getCause();
    }

    public Arrow getArrow() {
        return damage.getArrow();
    }

    public List<Skill> getSkills() {
        return damage.getSkills();
    }

    public ItemStack getItemInHand() {
        return attacker.getEquipment().getItemInHand();
    }
    public boolean isApplyKnockback() {
        return damage.isApplyKnockback();
    }

    public String getDeathMessage() {
        String withMsg = null;
        switch(getCause()) {
            case PROJECTILE:
                withMsg = ChatColor.YELLOW + "Archery";
                break;
            case SKILL:
                withMsg = ChatColor.DARK_AQUA + getSkills().get(0).getName();
                getSkills().remove(0);
                break;
            case MELEESKILL:
                withMsg = ChatColor.AQUA + getSkills().get(0).getName();
                getSkills().remove(0);
                break;
            case MELEE:
                if (getItemInHand() == null || getItemInHand().getItemMeta() == null) withMsg = "Fists";
                else withMsg = getItemInHand().getItemMeta().getDisplayName();
                break;
            case NULL:
                withMsg = ChatColor.DARK_PURPLE + "Magic?";
                break;
            default:
                throw new NullPointerException("deathapplyevent: 85");
        }
        if(getSkills().size() > 1) {
            StringBuilder builder = new StringBuilder();
            builder.append(withMsg);
            for (int i = 1; i < getSkills().size(); i++) {
                builder.append(", ");
                builder.append(ChatColor.YELLOW);
                builder.append(getSkills().get(i).getName());
            }
            withMsg = builder.toString();
        }

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.LIGHT_PURPLE);
        builder.append("Death> ");
        builder.append(ChatColor.RESET);
        builder.append(player.getDisplayName());
        builder.append(ChatColor.GRAY);
        builder.append(" was killed by ");
        builder.append(ChatColor.RESET);
        builder.append((attacker instanceof Player) ? ((Player) attacker).getDisplayName() : attacker.getName());
        builder.append(ChatColor.GRAY);
        builder.append(" using ");
        builder.append(ChatColor.RESET);
        builder.append(withMsg);
        builder.append(ChatColor.GRAY);
        builder.append(".");
        return builder.toString();
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
