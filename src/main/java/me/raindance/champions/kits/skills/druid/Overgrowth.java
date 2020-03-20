package me.raindance.champions.kits.skills.druid;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.events.skill.SkillInteractEvent;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Interaction;
import net.md_5.bungee.protocol.packet.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 206, skillType = SkillType.Druid, invType = InvType.SWORD)
public class Overgrowth extends Interaction implements ICooldown, IEnergy {
    public Overgrowth() {
        this.canMiss = false;
    }

    @EventHandler
    public void enemyCheck(SkillUseEvent e){
        if(e instanceof SkillInteractEvent) {
            SkillInteractEvent interact = (SkillInteractEvent) e;
            if(interact.getSkill().equals(this) && !isAlly(interact.getInteractor())) {
                getPlayer().sendMessage(String.format("%sSkill> %sOvergrowth %sdoes not affect your enemies.", ChatColor.BLUE, ChatColor.GREEN, ChatColor.GRAY));
                interact.setCancelled(true);
            }
        }
    }

    @Override
    public void doSkill(LivingEntity clickedEntity) {
        if(onCooldown()) return;

        StatusApplier.getOrNew(clickedEntity).applyStatus(Status.ABSORPTION, 5, 1);
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(clickedEntity.getLocation().toVector(), EnumWrappers.Particle.HEART,
                3, 0, 0.9f, 0);
        getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.enderdragon.wings", 0.8F, 1);
        setLastUsed(System.currentTimeMillis());
    }

    @Override
    public float getCooldown() {
        return 5;
    }

    @Override
    public int getEnergyUsage() {
        return 100;
    }

    @Override
    public String getName() {
        return "Overgrowth";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
