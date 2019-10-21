package me.raindance.champions.kits.skills.KnightSkills;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class HiltSmash extends Interaction {
    private final int MAX_LEVEL = 5;
    private float duration;
    private double damage;

    public HiltSmash(Player player, int level) {
        super(player, "Hilt Smash", level, SkillType.Knight, ItemType.SWORD, InvType.SWORD, 12 + level);
        this.damage = 4 + 0.5F * level;
        this.duration = -0.5F + 0.5f * level;
        setDesc(Arrays.asList(
                "Smash the hilt of your sword into ",
                "your opponent, dealing %%damage%% damage ",
                "and Slow 2 for %%time%% seconds."
        ));
        addDescArg("damage", () ->  damage);
        addDescArg("time", () -> duration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public void doSkill(Entity clickedEntity) {
        if (!onCooldown()) {
            if (clickedEntity instanceof Player) {
                Player victim = (Player) clickedEntity;
                if(!getPlayer().canSee(victim)) return;
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, duration, 1);
                DamageApplier.damage(victim, getPlayer(), damage, this, false);
                this.setLastUsed(System.currentTimeMillis());
                getPlayer().sendMessage(getUsedMessage(victim));
            }
        }
    }
}
