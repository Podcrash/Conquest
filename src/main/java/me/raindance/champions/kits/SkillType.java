package me.raindance.champions.kits;

import me.raindance.champions.inventory.ChampionsInventory;

import java.util.Arrays;

public enum SkillType {
    Vanguard("Vanguard", new int[]{801,807,806,808,803}),
    Berserker("Berserker", new int[]{107,102,108,106,105}),
    Duelist("Duelist", new int[]{308,305,307,304,309}),
    Warden("Warden", new int[]{905,902,901,906}),
    Marksman("Marksman", new int[]{502,505,501,504,506}),
    Hunter("Hunter", new int[]{408,406,404,403,405}),
    Sorcerer("Sorcerer", new int[]{1011,1001,1007,1006,1002}),
    Druid("Druid", new int[]{206,207,201,205,202}),
    Rogue("Rogue", new int[]{609,605,601,608,603}),
    Thief("Thief", new int[]{708,705,702,710,706}),
    Global("All", new int[]{});

    /**
     * Knight >> Duelist & Warden
     * Ranger >> Marksman & Hunter
     * Mage >> Sorcerer & Druid
     * Assassin >> Rogue & Thief
     */
    private String name;
    private int[] defaultSkills;

    SkillType(String name, int[] defaultSkills) {
        this.name = name;
        this.defaultSkills = defaultSkills;
    }

    public String getName() {
        return name;
    }

    public int[] getDefaultSkills() {
        return defaultSkills;
    }


    public static SkillType getByName(String name){
        name = name.toLowerCase();
        for(SkillType skillType : SkillType.values()) {
            if(name.contains(skillType.getName().toLowerCase())) return skillType;
        }
        return null;
    }
    @Override
    public String toString() {
        return getName();
    }

    private final static SkillType[] details = SkillType.values();
    public static SkillType[] details() {
        return details;
    }
}
