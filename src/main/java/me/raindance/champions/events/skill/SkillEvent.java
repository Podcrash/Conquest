package me.raindance.champions.events.skill;

import me.raindance.champions.Main;
import me.raindance.champions.kits.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Skill skill;

    public SkillEvent(Skill skill) {
        this.skill = skill;
        this.player = skill.getPlayer();
        Main.getInstance().getLogger().info("[SKILLDEGBUG] " + player.getName() + " used " + skill.getName());
    }

    public Player getPlayer() {
        return player;
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
