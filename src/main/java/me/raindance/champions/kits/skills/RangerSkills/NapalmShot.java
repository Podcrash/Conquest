package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import me.raindance.champions.time.resources.TimeResource;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NapalmShot extends BowShotSkill {
    private List<Item> items;
    private int flames;
    private int duration;
    private final String NAME;
    private Resetter resetter;
    public NapalmShot(Player player, int level) {
        super(player, "Napalm Shot", level, SkillType.Ranger, ItemType.BOW, InvType.BOW, 30 - 2 * level, false);
        this.flames = 8 + 8 * level;
        this.duration = 2 + level;
        NAME = (player != null) ? "NAPALM" + getPlayer().getName() : null;
        items = new ArrayList<>();
        resetter = new Resetter();
        setDesc(Arrays.asList(
                "Prepare a Napalm Shot: ",
                "",
                "Your next arrow will burst into ",
                "%%flames%% flames on impact. ",
                "",
                "If your arrow hit an enemy, it",
                "will ignite them for %%duration%% seconds. "
        ));
        addDescArg("flames", () ->  flames);
        addDescArg("duration", () -> duration);
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        arrow.setFireTicks(1000);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        event.setVelocityModifierX(1.4D);
        event.setVelocityModifierY(1.4D);
        event.setVelocityModifierZ(1.4D);
        victim.setFireTicks(0);
}

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        hit(arrow, location);
    }

    /**
     *
     * @param location where the arrow will land
     */
    private void hit(Arrow arrow, Location location){
        if(arrow.getFireTicks() <= 0) return;
        final Random random = new Random();
        double yoffset = 0.2;
        int section = flames / 4;
        Vector vector = new Vector(0D, yoffset * section/flames, 0D);
        for(int i = 0; i < flames; i++){
            Item item = ItemManipulationManager.regular(Material.BLAZE_POWDER, location, vector.setX(vector.getY() * FastMath.sin(i)).setZ(vector.getY() * FastMath.cos(i)));
            item.setCustomName("RITB");
            ItemStack itemStack = item.getItemStack();
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(NAME + (random.nextDouble() * System.currentTimeMillis()));
            itemStack.setItemMeta(meta);
            items.add(item);
            if(i > section) {
                section += flames/4;
                vector.setY(yoffset * section/flames);
            }
        }
        resetter.setTime(System.currentTimeMillis());
        resetter.run(1, 1);
    }

    private class Resetter implements TimeResource {
        private long time;
        private Resetter() {

        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public void task() {

        }

        @Override
        public boolean cancel() {
            return System.currentTimeMillis() - time >= 20000L || items.size() == 0;
        }

        @Override
        public void cleanup() {
            if(items.size() != 0) {
                for (Item item : items) {
                    if (item.isValid()) item.remove();
                }
                items.clear();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void pickUp(PlayerPickupItemEvent event){
        if(!items.contains(event.getItem())) return;
        Item item = event.getItem();
        if(item.isOnGround()){
            item.remove();
            if(event.getPlayer().getLocation().getBlock().getType().equals(Material.WATER)) return;
            StatusApplier.getOrNew(event.getPlayer()).applyStatus(Status.FIRE, duration, 1);
            DamageApplier.damage(event.getPlayer(), getPlayer(), 0.5, this, false);
            items.remove(item);
        }
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
