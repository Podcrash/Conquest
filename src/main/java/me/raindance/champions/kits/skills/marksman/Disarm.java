package me.raindance.champions.kits.skills.marksman;
import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.time.TimeHandler;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Interaction;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 509, skillType = SkillType.Marksman, invType = InvType.SWORD)
public class Disarm extends Interaction {
    private final float duration = 1F;
    private final double damage = 4;
    private LivingEntity disarmed = null;

    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public String getName() {
        return "Disarm";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    @Override
    public void doSkill(LivingEntity victim) {
        if (onCooldown()) return;
        if(isAlly(victim)) return;
        //getPlayer().sendMessage(getUsedMessage(victim));
        StatusApplier.getOrNew(victim).applyStatus(Status.WEAKNESS, duration, 99);
        DamageApplier.damage(victim, getPlayer(), damage, this, false);
        this.setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.spider.death", 0.9F, 77);
        victim.sendMessage(String.format("%sCondition> %sYou have been disarmed by %s%s%s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                getPlayer().getName(),
                ChatColor.GRAY));
        disarmed = victim;
        TimeHandler.delayTime(30, () -> disarmed = null);

        landed();
    }

    @EventHandler
    public void onHit(DamageApplyEvent e){
        if(!e.getAttacker().equals(disarmed) || !e.getVictim().equals(getPlayer())) return;
        e.setDoKnockback(false);
    }
}