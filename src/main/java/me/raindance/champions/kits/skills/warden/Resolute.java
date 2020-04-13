package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

//@SkillMetadata(id = 911, skillType = SkillType.Warden, invType = InvType.PRIMARY_PASSIVE)
public class Resolute extends Passive{

    private int radius = 6;
    private double reductionPerPlayer = 0.75;

    @Override
    public String getName() {
        return "Resolute";
    }

    @EventHandler
    public void onDamage(DamageApplyEvent e) {
        if(e.getVictim().equals(getPlayer()) && (e.getCause().equals(Cause.MELEE) || e.getCause().equals(Cause.MELEESKILL))) {
            double numEnemies = 0;
            for(Player p : BlockUtil.getPlayersInArea(getPlayer().getLocation(), radius, getPlayers())) {
                if(!isAlly(p)) numEnemies++;
            }

            e.setDamage(Math.max(e.getDamage() - (numEnemies * reductionPerPlayer), 0));
            e.setModified(true);
        }
    }
}
