package me.raindance.champions.kits.enums;

public enum SkillType {
    Brute("Brute"),
    Knight("Knight"),
    Ranger("Ranger"),
    Mage("Mage"),
    Assassin("Assassin"),
    Global("All");


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
}
