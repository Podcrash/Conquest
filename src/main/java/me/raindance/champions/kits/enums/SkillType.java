package me.raindance.champions.kits.enums;

import me.raindance.champions.kits.Skill;

public enum SkillType {
    Brute("Brute"), Vanguard("Vanguard"), Berserker("Berserker"),
    Knight("Knight"), Duelist("Duelist"), Warden("Warden"),
    Ranger("Ranger"), Marksman("Marksman"), Hunter("Hunter"),
    Mage("Mage"), Sorcerer("Sorcerer"), Druid("Druid"),
    Assassin("Assassin"), Rogue("Rogue"), Thief("Thief"),
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
