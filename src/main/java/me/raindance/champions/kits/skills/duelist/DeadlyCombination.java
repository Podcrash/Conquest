package me.raindance.champions.kits.skills.duelist;

import com.packetwrapper.abstractpackets.AbstractPacket;
import com.packetwrapper.abstractpackets.WrapperPlayServerEntityStatus;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.sound.SoundPlayer;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Interaction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 303, skillType = SkillType.Duelist, invType = InvType.SWORD)
public class DeadlyCombination extends Interaction implements ICooldown {
    private LivingEntity attacked = null;
    private int i;
    @Override
    public void doSkill(LivingEntity clickedEntity) {
        if (onCooldown()) return;
        if (isAlly(clickedEntity)) return;
        this.attacked = clickedEntity;
        this.i = 0;
        setLastUsed(System.currentTimeMillis());
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(attacked.getEntityId());
        packet.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);

        AbstractPacket packet2 = ParticleGenerator.createBlockEffect(attacked.getLocation().toVector(), Material.OBSIDIAN.getId());
        for (Player player : getPlayers()) {
            packet.sendPacket(player);
            packet2.sendPacket(player);
        }

        landed();
    }

    @EventHandler
    public void hit(DamageApplyEvent event) {
        if (attacked == null || event.getVictim() != attacked || event.getAttacker() != getPlayer()) return;
        i++;

        if (i < 2 || System.currentTimeMillis() - getLastUsed() > 3000L) return;
        StatusApplier.getOrNew(attacked).applyStatus(Status.SLOW, 3, 2);
        event.setDamage(event.getDamage() + 2);
        event.setModified(true);
        event.addSource(this);
        getPlayer().sendMessage(getUsedMessage(event.getVictim()).replace("used", "unleashed"));
        event.getVictim().sendMessage(String.format("%sCondition> %s%s%s unleashed %sDeadly Combination %son you.",
                ChatColor.BLUE,
                ChatColor.YELLOW,
                getPlayer().getName(),
                ChatColor.GRAY,
                ChatColor.GREEN,
                ChatColor.GRAY));
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.enderdragon.hit", 1, 63);

        attacked = null;
    }


    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public String getName() {
        return "Deadly Combination";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
