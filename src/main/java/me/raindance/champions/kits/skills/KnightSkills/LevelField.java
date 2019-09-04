package me.raindance.champions.kits.skills.KnightSkills;

import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class LevelField extends Passive implements TimeResource, IPassiveTimer {
    private int bonus;
    private int distance;

    public LevelField(Player player, int level) {
        super(player, "Level Field", level,  SkillType.Knight, InvType.PASSIVEB);
        this.bonus = 0;
        this.distance = 4 + 2 * level;
        this.distance *= distance;
        setDesc(Arrays.asList(
                "You deal X more damage. ",
                "You take X less damage. ",
                "X = (Nearby Enemies) - (Nearby Allies) - 1",
                "Players within %%distance%% blocks are considered. ",
                "",
                "You can not deal less damage, or take ",
                "more damage via this skill."
        ));
        addDescArg("distance", () ->  Math.sqrt(distance));
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        //calculate(); if you wanted to make it on demand instead of calculating it every few ticks
        if (event.getAttacker() == getPlayer()) {
            if(bonus == 0) return;
            event.addSkillCause(this);
            event.setDamage(event.getDamage() + bonus);
        }
        else if (event.getVictim() == getPlayer()){
            event.setDamage(event.getDamage() - bonus);
            if(event.getDamage() < 0) event.setDamage(0);
        }
        event.setModified(true);
    }

    /** Why bonus starts at -1:
     * +---------+--------+--------------------------------------------+
     * | Enemies | Allies | Bonus [0, infinity) (Enemies - allies - 1) |
     * +---------+--------+--------------------------------------------+
     * |    1    |    0   |                      0                     |
     * +---------+--------+--------------------------------------------+
     * |    2    |    0   |                      1                     |
     * +---------+--------+--------------------------------------------+
     * |    1    |    3   |                      0                     |
     * +---------+--------+--------------------------------------------+
     * |    3    |    2   |                      0                     |
     * +---------+--------+--------------------------------------------+
     * |    3    |    1   |                      1                     |
     * +---------+--------+--------------------------------------------+
     */
    private void calculate() {
        Location location = getPlayer().getLocation();
        ChampionsPlayer cplayer = getChampionsPlayer();
        bonus = -1;
        for (Player player : getPlayers()) {
            if (player == getPlayer() || location.distanceSquared(player.getLocation()) > distance) continue;
            if(cplayer.isAlly(player)) bonus--;
            else bonus++;
            if(bonus < 0) bonus = 0;
        }
    }


    @Override
    public void start() {
        run(7, 100);
    }

    @Override
    public void task() {
        calculate();
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }
}
