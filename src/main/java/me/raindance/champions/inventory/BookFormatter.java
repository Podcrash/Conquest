package me.raindance.champions.inventory;

import me.raindance.champions.Main;
import me.raindance.champions.kits.Skill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class BookFormatter {
    private Skill skill;
    private final String name;
    private final int maxLevel;
    private final float cooldown;
    private final int skillTokenWeight;

    private Constructor constructor; //cache the skill constructor
    private final List<List<String>> descriptions = Collections.synchronizedList(new ArrayList<>());

    public BookFormatter(Skill skill) {
        this.skill = skill;
        this.name = skill.getName();
        this.maxLevel = skill.getMaxLevel();
        this.cooldown = skill.getCooldown();
        this.skillTokenWeight = skill.getSkillTokenWeight();
        try {
            this.constructor = skill.getClass().getDeclaredConstructor(Player.class, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (this.constructor == null) {
            Main.getInstance().log.info("THIS CANNOT BE PASSED, from bookformatter");
        } else {
            try {
                setDescs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (skill.getPlayer() != null) throw new RuntimeException("Be warned, player is defined");
    }

    @Override
    public String toString() {
        return String.format("%s %d %s %.2f %s", skill.getName(), skill.getMaxLevel(), skill.getDescription(), skill.getCooldown(), skill.getSkillTokenWeight());
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    private void setDescs() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(maxLevel);
        Main.getInstance().log.info("Loading descriptions for " + skill.getName());
        List<Callable<List<String>>> callables = new ArrayList<>();
        for (int i = 1; i <= maxLevel; i++) {
            //what we are going to do is set up multiple skill constructors and extract the descriptions from them.
            //Fastest way to do it is async!
            final int currentLevel = i;
            callables.add(() -> {
                Skill skillObj;
                try {
                    skillObj = (Skill) constructor.newInstance(null, currentLevel);
                    return skillObj.getDescription();
                    //simply adding it without the index is valid, but this is safer

                } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
                throw new NullPointerException("from bookformatter:76");
            });
        }
        for (Future<List<String>> desc : executor.invokeAll(callables)) {
            descriptions.add(desc.get());
        }

        executor.shutdown();
    }

    private void setDescsSync() {
        for (int i = 0; i <= maxLevel; i++) {
            //what we are going to do is set up multiple skill constructors and extract the descriptions from them.
            //Fastest way to do it is async!
            final int currentLevel = i;
            Skill skillObj;
            try {
                skillObj = (Skill) constructor.newInstance(null, currentLevel);
                descriptions.add(currentLevel, skillObj.getDescription());
                //simply adding it without the index is valid, but this is safer

            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            throw new NullPointerException("from bookformatter:76");
        }
    }

    public List<String> getDescription(int level) {
        return descriptions.get(level);
    }

    public float getCooldown() {
        return cooldown;
    }

    public int getSkillTokenWeight() {
        return skillTokenWeight;
    }

    public String getHeader(int currentLevel) {
        return String.format(ChatColor.BOLD + ChatColor.GREEN.toString() + "%s %d/%d", name, currentLevel, maxLevel);
    }

    public Skill getSkill() {
        if (skill != null) return this.skill;
        else {
            Set<Skill> skills = InventoryData.getIdSkillMap().values();
            for(Skill skill : skills){
                if(skill.getName().equalsIgnoreCase(name)) {
                    return skill;
                }
            }
            return null;
        }
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Skill newInstance(Player player, int level) {
        try {
            return (Skill) constructor.newInstance(player, level);
        }catch (IllegalAccessException|InstantiationException|InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("the object returned must not be null");
    }
}
