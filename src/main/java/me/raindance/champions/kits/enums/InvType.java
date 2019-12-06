package me.raindance.champions.kits.enums;

public enum InvType {
    SWORD("Sword"), //sword
    AXE("Axe"), //axe
    SHOVEL("Shovel"), //shovel
    BOW("Bow"), //bow
    PASSIVEA("Passive A"), //primary
    PASSIVEB("Passive B"), //secondary
    INNATE("Innate"), //Innate
    DROP("Active");

    private String name;
    InvType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    private final static InvType[] details = InvType.values();
    public static InvType[] details() {
        return details;
    }
}
