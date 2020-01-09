package me.raindance.champions.kits.skills.thief;

import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.EntityUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

@SkillMetadata(id = 705, skillType = SkillType.Thief, invType = InvType.AXE)
public class Leap extends Instant implements ICooldown {
    @Override
    public float getCooldown() {
        return 4.5F;
    }

    @Override
    public String getName() {
        return "Leap";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if (!rightClickCheck(action)) return;
        Player player = getPlayer();
        //idk the proper value
        //double velocity = 1.3D + 0.2D * getLevel();
        Vector behind = getPlayer().getLocation().getDirection().normalize().multiply(-1);
        Location loc = getPlayer().getLocation().clone().add(behind.setY(0));
        if (!loc.getBlock().getType().equals(Material.AIR)) {
            player.sendMessage(String.format("%sSkill> %sYou used %sWall Kick%s.",
                    ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
            SoundPlayer.sendSound(player.getLocation(), "mob.bat.takeoff", 1, 90);
            Vector v = getPlayer().getLocation().getDirection().multiply(1.2);
            v.setY(0);
            v.multiply(0.7);
            v.setY(v.getY() + 0.7);
            if(v.getY() > 2) v.setY(2);
            if(EntityUtil.onGround(player)) v.setY(v.getY() + 0.2);
            player.setVelocity(v);
            player.setFallDistance(-3);
        }else {
            if(!onCooldown()) {
                player.sendMessage(getUsedMessage());
                SoundPlayer.sendSound(player.getLocation(), "mob.bat.takeoff", 1, 70);
                Vector v = getPlayer().getLocation().getDirection().multiply(1.2);
                v.setY(v.getY() + 0.2);
                if(v.getY() > 1) v.setY(1);
                else if (v.getY() < 0) v.setY(0);
                if(EntityUtil.onGround(getPlayer())) v.setY(v.getY() + 0.2);
                player.setVelocity(v);
                player.setFallDistance(-3);
                this.setLastUsed(System.currentTimeMillis());
            } //else player.sendMessage(getCooldownMessage());
        }
    }
}
