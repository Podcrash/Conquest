package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@SkillMetadata(id = 204, skillType = SkillType.Druid, invType = InvType.AXE)
public class NaturesGift extends Instant implements ICooldown, IEnergy {
    @Override
    public float getCooldown() {
        return 20;
    }

    @Override
    public int getEnergyUsage() {
        return 100;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        getGame().consumeBukkitPlayer(this::buff);
        useEnergy(getEnergyUsage());
        setLastUsed(System.currentTimeMillis());

    }

    private void buff(Player victim) {
        if(victim != getPlayer() && !isAlly(victim)) return;
        if(victim.getLocation().distanceSquared(getPlayer().getLocation()) > 25) return;

        StatusApplier.getOrNew(victim).applyStatus(Status.REGENERATION, 3, 0, false);
    }
    @Override
    public String getName() {
        return "Nature's Gift";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }
}
