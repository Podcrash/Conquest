package me.raindance.champions.kits.skills.KnightSkills;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class RopedAxeThrow extends Instant {
    private float veloScale;
    private float damage;
    private final String identifier;
    private int invSlot;
    public RopedAxeThrow(Player player, int level) {
        super(player, "Roped Axe Throw", level, SkillType.Knight, ItemType.AXE, InvType.AXE, (float)((int)((4.3F - 0.3F * level) * 100.0))/100.0f);
        this.veloScale = 0.7F + 0.1F * level;
        this.damage = 4.5F + 0.5F * level;
        this.identifier = getPlayer() == null ? null : getPlayer().getName() + "roped_axe_axe";
        setDesc(Arrays.asList(
                "Throw your axe with %%velocity%% velocity, ",
                "dealing %%damage%% damage. ",
                "",
                "You pull your axe back to you when it ",
                "collides with anything. "
        ));
        addDescArg("damage", () ->  damage);
        addDescArg("velocity", () -> veloScale);
    }

    private ItemStack itemStack;

    @Override
    public int getMaxLevel() {
        return 5;
    }
    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (action != (Action.RIGHT_CLICK_AIR) && action != Action.RIGHT_CLICK_BLOCK) return;
        if (onCooldown()) {
            return;
        }
        Vector vector = getPlayer().getLocation().getDirection().normalize().multiply(veloScale);
        invSlot = getPlayer().getInventory().getHeldItemSlot();
        itemStack = getPlayer().getInventory().getItemInHand();
        getPlayer().getInventory().setItem(invSlot, null);
        ItemManipulationManager.intercept(getPlayer(), itemStack.getType(), getPlayer().getEyeLocation(), vector,
                (item, entity) -> {
                    if (entity == null) {// not hit

                    }else {
                        if(entity instanceof Player){
                            DamageApplier.damage(entity, getPlayer(), this.damage, true);
                        }else entity.damage(damage);
                    }
                    item.setCustomName(identifier);
                    item.setVelocity(getPlayer().getEyeLocation().subtract(item.getLocation()).toVector().normalize().multiply(veloScale));
                });
        this.setLastUsed(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickUp(PlayerPickupItemEvent event){
        if(event.getPlayer() != getPlayer()) {
            event.setCancelled(true);
        }
        else if(event.getPlayer() == getPlayer() && event.getItem().getCustomName() != null && event.getItem().getCustomName().equals(identifier)){
            getPlayer().getInventory().setItem(invSlot, itemStack);
            event.setCancelled(true);
            event.getItem().remove();
        }
    }
}
