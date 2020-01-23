package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

@SkillMetadata(id = 203, skillType = SkillType.Druid, invType = InvType.SHOVEL)
public class Miasma extends Instant implements IEnergy, TimeResource, ICooldown {
    private final float duration = 10;
    public void toggle() {
        run(1, 0);
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        toggle();
    }

    @Override
    public float getCooldown() {
        return 22;
    }

    @Override
    public String getName() {
        return "Miasma";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    @Override
    public int getEnergyUsage() {
        return 80;
    }

    @Override
    public void task() {

        //TODO: Particles
        getGame().consumeBukkitPlayer(this::debuff);
    }

    private void debuff(Player victim) {
        if(victim == getPlayer() || isAlly(victim)) return;
        if(victim.getLocation().distanceSquared(getPlayer().getLocation()) > 25) return;

        StatusApplier.getOrNew(victim).applyStatus(Status.POISON, 2, 0, false);
        StatusApplier.getOrNew(victim).applyStatus(Status.DIZZY, 2, 0, false);
    }
    @Override
    public boolean cancel() {
        return System.currentTimeMillis() - getLastUsed() > duration;
    }

    @Override
    public void cleanup() {
        getPlayer().sendMessage(getUsedMessage().replace("used", "ended"));
    }
}
