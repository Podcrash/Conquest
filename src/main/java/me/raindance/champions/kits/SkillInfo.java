package me.raindance.champions.kits;

import com.google.common.reflect.ClassPath;
import me.raindance.champions.inventory.SkillData;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skills.warden.Adrenaline;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Class that is going to be used to store and organize all skills.
 */
public final class SkillInfo {
    private final static List<SkillData> skillData = new ArrayList<>();

    //wondering if I should keep this.
    private final static Map<Integer, SkillData> idDataMap = new HashMap<>();
    private final static Map<SkillType, int[]> skillTypeCache = new EnumMap<>(SkillType.class);
    private final static Map<InvType, int[]> invTypeCache = new EnumMap<>(InvType.class);

    public static void setUp() {
        System.out.println("Loading classes");
        try {
            List<String> list = Arrays.asList("warden", "duelist", "vanguard", "berserker", "marksman", "hunter", "thief");
            for(String e : list)
                SkillInfo.addSkills(e);
            skillData.sort((s1, s2) -> Integer.compare(s2.getId(), s1.getId()));
            System.out.println("Sorted classes");
        }catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void addSkills(String championName) throws IOException, ClassNotFoundException {
        String path = "me.raindance.champions.kits.skills." + championName;
        ClassPath cp = ClassPath.from(Adrenaline.class.getClassLoader());
        Set<ClassPath.ClassInfo> classInfoSet = cp.getTopLevelClasses(path);
        for(ClassPath.ClassInfo info : classInfoSet) {
            System.out.println(info.getName());
            Class<?> skillClass = Class.forName(info.getName());

            Skill skill = (Skill) emptyConstructor(skillClass);
            if(skill == null) throw new RuntimeException("skill cannot be null! current at: " + info.getName());
            SkillMetadata annot = skillClass.getAnnotation(SkillMetadata.class);
            SkillType skillType = annot.skillType();
            InvType invType = annot.invType();

            addSkill(skillType, invType, skill);
        }
    }
    private static void addSkill(SkillType skillType, InvType invType, Skill skill) {
        //TODO: put more of this information in the annotations.
        SkillData data = new SkillData(skill, skill.getID(), skill.getName(), invType, skillType);
        System.out.println(data);
        skillData.add(data);
    }

    private static <T> T emptyConstructor(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        }catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getSkillID(Skill skill) {
        for(SkillData data : skillData) {
            if(data.getName().equalsIgnoreCase(skill.getName()))
                return data.getId();
        }
        return -1;
    }

    public static SkillData getSkill(int id) {
        SkillData data;
        if((data = idDataMap.get(id)) == null) {
            for (SkillData data4 : skillData) {
                if(id == data4.getId()) {
                    data = data4;
                    break;
                }
            }
        }
        return data;
    }
    /**
     * Used for lazy iteration
     * @param skillType
     * @param skillConsumer
     */
    public static void skillsConsumer(SkillType skillType, Consumer<SkillData> skillConsumer) {
        for(SkillData data : skillData)
            if(data.getSkillType() == skillType)
                skillConsumer.accept(data);
    }
    public static void invsConsumer(InvType invType, Consumer<SkillData> skillConsumer) {
        for(SkillData data : skillData)
            if(data.getInvType() == invType)
                skillConsumer.accept(data);
    }
    public static void skillInvConsumer(SkillType skillType, InvType invType, Consumer<SkillData> skillConsumer) {
        for(SkillData data : skillData)
            if(data.getInvType() == invType && data.getSkillType() == skillType)
                skillConsumer.accept(data);
    }

    public static List<SkillData> getSkills(SkillType skillType) {
        if(skillTypeCache.get(skillType) == null) cacheSkillType(skillType);
        List<SkillData> datas = new ArrayList<>();
        for(int index : skillTypeCache.get(skillType))
            datas.add(skillData.get(index));
        return datas;
    }
    public static List<SkillData> getSkills(InvType invType) {
        if(invTypeCache.get(invType) == null) cacheInvType(invType);
        List<SkillData> datas = new ArrayList<>();
        for(int index : invTypeCache.get(invType))
            datas.add(skillData.get(index));
        return datas;
    }

    private static void cacheSkillType(SkillType skillType) {
        List<Integer> intList = new ArrayList<>();
        for(int i = 0, size = skillData.size(); i < size; i++) {
            SkillData data = skillData.get(i);
            if(data.getSkillType() == skillType)
                intList.add(i);
        }
        int[] ids = new int[intList.size()];
        for(int i = 0; i < ids.length; i++) ids[i] = intList.get(i);
        skillTypeCache.put(skillType, ids);
    }
    private static void cacheInvType(InvType invType) {
        List<Integer> intList = new ArrayList<>();
        for(int i = 0, size = skillData.size(); i < size; i++) {
            SkillData data = skillData.get(i);
            if(data.getInvType() == invType)
                intList.add(i);
        }
        int[] ids = new int[intList.size()];
        for(int i = 0; i < ids.length; i++) ids[i] = intList.get(i);
        invTypeCache.put(invType, ids);
    }

    public static List<SkillData> getSkillData() {
        return skillData;
    }
}
