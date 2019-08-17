package me.raindance.champions.effect.particle;

import me.raindance.champions.Main;
import me.raindance.champions.time.resources.TimeResource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleRunnable implements TimeResource {
    static ParticleRunnable particleRunnable;
    private List<EntityParticleWrapper> wrappers = new ArrayList<>();
    private boolean active = false;
    private ParticleRunnable() {
        particleRunnable = this;
        active = true;
        Main.instance.getLogger().info("[ParticleRunnable]: Starting!");
    }

    public static void start(){
        new ParticleRunnable().run(1,0);
    }

    public static void stop(){
        particleRunnable.setActive(false);
    }

    @Override
    public void task() {
        if(wrappers.size() > 0) {
            for(final Iterator<EntityParticleWrapper> wrapperIterator = wrappers.iterator(); wrapperIterator.hasNext(); ){
                EntityParticleWrapper wrapper = wrapperIterator.next();
                if (!wrapper.cancel()) {
                    wrapper.send();
                }
            }
            wrappers.removeIf(entityParticleWrapper -> !entityParticleWrapper.getEntity().isValid());
        }
    }

    @Override
    public boolean cancel() {
        return !active;
    }

    @Override
    public void cleanup() {
        Main.instance.getLogger().info("[ParticleRunnable]: Shutting off!");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<EntityParticleWrapper> getWrappers() {
        return wrappers;
    }
}
