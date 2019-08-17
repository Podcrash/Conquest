package me.raindance.champions.kits.skills.KnightSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Random;

public class Fortitude extends Passive implements TimeResource {

    private final int MAX_LEVEL = 3;
    private final Random rand = new Random();
    private int lostHealth;
    private float perSecond;
    private final long perSecondTicks;
    private int delay;
    private final long delayMilles;
    private boolean currentlyRunning;

    private double damageRecieved;
    public Fortitude(Player player, int level) {
        super(player, "Fortitude", level, SkillType.Knight, InvType.PASSIVEB, -1);
        this.lostHealth = level;
        this.perSecond = 3.0F - (0.5F * level);
        this.perSecondTicks = (long) (20F * this.perSecond);
        this.delay = 3 + level;
        this.delayMilles = 1000L * this.delay;
        this.currentlyRunning = false;
        this.damageRecieved = 0D;
        setDesc(Arrays.asList(
                "After taking damage, you regenerate ",
                "up to %%losthealth%% of the health you lost. ",
                "",
                "You restore health at a rate of ",
                "1 health per %%rate%% seconds. ",
                "",
                "This does not stack, and is reset if ",
                "you are hit again. "
        ));
        addDescArg("losthealth", () ->  lostHealth);
        addDescArg("rate", () -> perSecond);
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void hit(DamageApplyEvent event) {
        if (event.getVictim() == getPlayer() && event.getCause() == Cause.MELEE) {
            add(event.getDamage());
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void hit(EntityDamageEvent event) {
        if (event.getEntity() == getPlayer() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            add(event.getDamage());
        }
    }

    private void add(double damage) {
        currentlyRunning = false;
        this.setLastUsed(System.currentTimeMillis());
        TimeHandler.unregister(this);
        run(15, 0);
        this.damageRecieved = (damage > lostHealth) ? lostHealth : damage;
    }

    @Override
    public void task() {
        //do nothing
    }

    @Override
    public boolean cancel() {
        return System.currentTimeMillis() - getLastUsed() >= this.delayMilles;
    }

    @Override
    public void cleanup() {
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(getPlayer().getLocation(), EnumWrappers.Particle.HEART,
                3, rand.nextFloat(), 0.9f, rand.nextFloat());
        getPlayers().forEach(player -> ParticleGenerator.generate(player, packet));
        currentlyRunning = true;
        new RestorePerSecond(this.damageRecieved).run(this.perSecondTicks, 0);
    }

    private final class RestorePerSecond implements TimeResource {
        private double damage;
        private int a = 0;
        private long time = System.currentTimeMillis();
        private RestorePerSecond(double damage){
            this.damage = damage;
        }

        @Override
        public void task() {
            time = System.currentTimeMillis();
            if(a < this.damage) getChampionsPlayer().heal(1D);
            else currentlyRunning = false;
            a++;
        }

        @Override
        public boolean cancel() {
            return !currentlyRunning;
        }

        @Override
        public void cleanup() {

        }
    }
}
