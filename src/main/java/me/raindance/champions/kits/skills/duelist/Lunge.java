package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.EntityUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

@SkillMetadata(id = 305, skillType = SkillType.Duelist, invType = InvType.DROP)
public class Lunge extends Drop implements ICooldown {
    private float cooldown = 8F;
    @Override
    public String getName() {
        return "Lunge";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if(onCooldown()) return false;
        setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "item.fireCharge.use", 0.8F, 90);
        setVector();
        return true;
    }

    private void setVector() {
        getPlayer().setFallDistance(-1);
        Vector vector = getPlayer().getLocation().getDirection();
        vector = vector.normalize().multiply(0.5d + 1.35d * 0.25D);
        vector.setY(vector.getY() + 0.4);
        double yMax = 0.5d + 0.82d * 0.1D;
        if (vector.getY() > yMax) vector.setY(yMax);
        if (EntityUtil.onGround(getPlayer())) vector.setY(vector.getY() + 0.2);
        getPlayer().setVelocity(vector);
    }

}
