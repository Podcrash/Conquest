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

    //The reason why this is written out so that it stays in order.
    private final static InvType[] details = new InvType[] {SWORD, AXE, SHOVEL, DROP, BOW, PASSIVEA, PASSIVEB, INNATE};;

    private String name;
    InvType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static InvType[] details() {
        return details;
    }
}
