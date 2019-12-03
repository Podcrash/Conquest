package me.raindance.champions.kits.skills.warden;

import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@SkillMetadata(skillType = SkillType.Warden, invType = InvType.SWORD)
public class EarthSmash extends Instant implements ICooldown {
    @Override
    public float getCooldown() {
        return 16;
    }

    @EventHandler
    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action)) return;
        //TODO: do stuff
    }

    @Override
    public String getName() {
        return "Earth Smash";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
