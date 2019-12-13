package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.EntityUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.DROP)
public class Lunge extends Drop implements ICooldown {
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
        return 6;
    }

    @Override
    public void drop(PlayerDropItemEvent e) {
        if(onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "item.fireCharge.use", 0.8F, 90);
        setVector();

    }

    private void setVector() {
        getPlayer().setFallDistance(-1);
        Vector vector = getPlayer().getLocation().getDirection();
        vector = vector.normalize().multiply(0.5d + 1.35d * 0.1D);
        vector.setY(vector.getY() + 0.2);
        double yMax = 0.5d + 0.82d * 0.1D;
        if (vector.getY() > yMax) vector.setY(yMax);
        if (EntityUtil.onGround(getPlayer())) vector.setY(vector.getY() + 0.2);
        getPlayer().setVelocity(vector);
    }

}
