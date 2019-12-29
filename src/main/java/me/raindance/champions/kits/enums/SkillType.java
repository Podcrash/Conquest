package me.raindance.champions.kits.enums;

import me.raindance.champions.kits.Skill;

public enum SkillType {
    Vanguard("Vanguard"), Berserker("Berserker"),
    Duelist("Duelist"), Warden("Warden"),
    Marksman("Marksman"), Hunter("Hunter"),
    Sorcerer("Sorcerer"), Druid("Druid"),
    Rogue("Rogue"), Thief("Thief"),
    Global("All");

    /**
     * Knight >> Duelist & Warden
     * Ranger >> Marksman & Hunter
     * Mage >> Sorcerer & Druid
     * Assassin >> Rogue & Thief
     */
    private String name;

    SkillType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
