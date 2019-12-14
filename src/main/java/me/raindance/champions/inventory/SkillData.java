package me.raindance.champions.inventory;

import com.podcrash.api.db.DataTableType;
import com.podcrash.api.db.DescriptorTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.redis.Communicator;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SkillData {
    private final int id;
    private final String name;
    private final InvType invType;
    private final SkillType skillType;
    private final Constructor<Skill> constructor;

    private List<String> description;

    public SkillData(Skill skill, int id, String name, InvType invType, SkillType skillType) {
        this.id = id;
        this.name = name;
        this.invType = invType;
        this.skillType = skillType;

        this.constructor = initConstructor(skill);

        requestDescription();
    }

    private String getCleanName() {
        return getName().toLowerCase().replace(" ", "");
    }
    private void requestDescription() {
        String cache = Communicator.getCacheValue(getCleanName());
        if(cache == null) {
            DescriptorTable table = TableOrganizer.getTable(DataTableType.DESCRIPTIONS);
            if(table == null) {
                this.description = Arrays.asList("Error loading skill descriptions!", "null");
                return;
            }
            table.requestCache(getCleanName());
            String value = table.getValue(getCleanName());
            if(value == null || value.isEmpty())
                this.description = Arrays.asList("Error loading skill descriptions!", "null");
            else this.description = Arrays.asList(value.split("\n"));
        }else this.description = Arrays.asList(cache.split("\n"));
    }

    private Constructor<Skill> initConstructor(Skill skill) {
        try {
            return (Constructor<Skill>) skill.getClass().getConstructor(null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Skill newInstance() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        if(description.get(description.size() - 1).equalsIgnoreCase("null"))
            requestDescription();
        return description;
    }

    public InvType getInvType() {
        return invType;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillData that = (SkillData) o;
        return Objects.equals(name, that.name) &&
                invType == that.invType &&
                skillType == that.skillType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, invType, skillType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SkillData{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description=").append(description);
        sb.append(", invType=").append(invType);
        sb.append(", skillType=").append(skillType);
        sb.append('}');
        return sb.toString();
    }
}
