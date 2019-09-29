package me.raindance.champions.inventory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.raindance.champions.Main;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skills.AssassinSkills.*;
import me.raindance.champions.kits.skills.BruteSkills.*;
import me.raindance.champions.kits.skills.GlobalSkills.BreakFall;
import me.raindance.champions.kits.skills.GlobalSkills.Resistance;
import me.raindance.champions.kits.skills.GlobalSkills.Swim;
import me.raindance.champions.kits.skills.KnightSkills.*;
import me.raindance.champions.kits.skills.MageSkills.*;
import me.raindance.champions.kits.skills.MageSkills.Void;
import me.raindance.champions.kits.skills.RangerSkills.*;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryData {

    private static BiMap<Integer, Skill> idSkillMap = HashBiMap.create();
    private static HashMap<Integer, BookFormatter> idUserReadMap = new HashMap<>();

    private static Set<Integer> bruteSet = new HashSet<>();
    private static Set<Integer> knightSet = new HashSet<>();
    private static Set<Integer> rangerSet = new HashSet<>();
    private static Set<Integer> mageSet = new HashSet<>();
    private static Set<Integer> assassinSet = new HashSet<>();
    private static Set<Integer> globalSet = new HashSet<>();

    private static Set<Integer> swordSet = new HashSet<>();
    private static Set<Integer> axeSet = new HashSet<>();
    private static Set<Integer> bowSet = new HashSet<>();
    private static Set<Integer> passiveASet = new HashSet<>();
    private static Set<Integer> passiveBSet = new HashSet<>();
    private static Set<Integer> passiveCSet = new HashSet<>();


    public static void addAssassin() {
        Main.getInstance().getLogger().info("Trying to add assassin");
        addSkill(new Illusion(null, 0));
        addSkill(new Evade(null, 0));
        addSkill(new Flash(null, 0));
        addSkill(new Blink(null, 0));
        addSkill(new Leap(null, 0));
        addSkill(new SmokeArrow(null, 0));
        addSkill(new SilencingArrow(null, 0));
        addSkill(new MarkedForDeath(null, 0));
        addSkill(new SmokeBomb(null, 0));
        addSkill(new Recall(null, 0));
        addSkill(new ComboAttack(null, 0));
        addSkill(new ShockingStrikes(null, 0));
        addSkill(new ViperStrikes(null, 0));
        addSkill(new Backstab(null, 0));
    }
    public static void addRanger() {
        Main.getInstance().getLogger().info("Trying to add ranger");
        addSkill(new WolfsPounce(null, 0));
        addSkill(new Disengage(null, 0));
        addSkill(new Agility(null, 0));
        addSkill(new WolfsFury(null, 0));
        addSkill(new RopedArrow(null, 0));
        addSkill(new HeavyArrows(null, 0));
        addSkill(new Longshot(null, 0));
        addSkill(new Sharpshooter(null, 0));
        addSkill(new VitalitySpores(null, 0));
        addSkill(new Overcharge(null, 0));
        addSkill(new PinDown(null, 0));
        addSkill(new BarbedArrows(null, 0));
        addSkill(new NapalmShot(null, 0 ));
        addSkill(new HealingShot(null,  0));
        addSkill(new ExplosiveArrow(null, 0));
        addSkill(new IncendiaryShot(null, 0));
        addSkill(new HeartsEye(null, 0 ));
        addSkill(new Barrage(null, 0));
    }
    public static void addMage() {

        Main.getInstance().getLogger().info("Trying to add mage");
        addSkill(new Rupture(null, 0));
        addSkill(new Inferno(null, 0));
        addSkill(new StaticLaser(null, 0));
        addSkill(new FireBlast(null, 0));
        addSkill(new SeismicBlade(null, 0));
        addSkill(new ArticArmor(null, 0));
        addSkill(new NullBlade(null, 0));
        addSkill(new MagmaBlade(null, 0));
        addSkill(new GlacialBlade(null, 0));
        addSkill(new Immolate(null, 0));
        addSkill(new Void(null, 0));
        addSkill(new IcePrison(null, 0));
        addSkill(new Fissure(null, 0));
        addSkill(new LightningOrb(null, 0));
        addSkill(new Blizzard(null, 0));
        addSkill(new ManaPool(null, 0));
    }
    public static void addKnight() {
        Main.getInstance().getLogger().info("Trying to add knight");
        addSkill(new DefensiveStance(null, 0));
        addSkill(new BullsCharge(null, 0));
        addSkill(new HiltSmash(null, 0));
        addSkill(new Riposte(null, 0));
        addSkill(new Deflection(null, 0));
        addSkill(new Swordsmanship(null, 0));
        addSkill(new HoldPosition(null, 0));
        addSkill(new Fortitude(null, 0));
        addSkill(new LevelField(null, 0));
        addSkill(new Vengeance(null,0));
        addSkill(new ShieldSmash(null, 0));
        addSkill(new Cleave(null, 0));
        addSkill(new RopedAxeThrow(null, 0));
    }
    public static void addBrute() {
        Main.getInstance().getLogger().info("Trying to add brute");
        addSkill(new Colossus(null, 0));
        addSkill(new Overwhelm(null, 0));
        addSkill(new CripplingBlow(null, 0));
        addSkill(new SeismicSlam(null, 0));
        addSkill(new Takedown(null, 0));
        addSkill(new FleshHook(null, 0));
        addSkill(new Stampede(null, 0));
        addSkill(new Bloodlust(null, 0));
        addSkill(new Intimidation(null, 0));
        addSkill(new DwarfToss(null, 0));
        addSkill(new WhirlwindAxe(null, 0));
    }
    public static void addGlobal() {
        Main.getInstance().getLogger().info("Trying to add global");
        addSkill(new Swim(null, 0));
        addSkill(new Resistance(null, 0));
        addSkill(new BreakFall(null, 0));
    }

    /**
     * Synchronize a skill with an id as well as put it in its proper class and item sets
     * @param skill
     */
    private static void addSkill(Skill skill) {
        if (skill.getPlayer() != null) {
            throw new RuntimeException(String.format("%s must have an empty player", skill.getName()));
        }
        Main.getInstance().getLogger().info(String.format("[SKILL] Loading %s: %s.", skill.getType(), skill.getName()));
        int id = skill.getID();
        idSkillMap.put(skill.getID(), skill);
        idUserReadMap.put(skill.getID(), new BookFormatter(skill));
        putClassSet(id, skill.getType());
        putItemSet(id, skill.getInvType());
    }
    private static void putClassSet(int id, SkillType stype) {
        Set<Integer> addSet = null;
        switch (stype) {
            case Knight:
                addSet = knightSet;
                break;
            case Assassin:
                addSet = assassinSet;
                break;
            case Ranger:
                addSet = rangerSet;
                break;
            case Global:
                addSet = globalSet;
                break;
            case Mage:
                addSet = mageSet;
                break;
            case Brute:
                addSet = bruteSet;
                break;
        }
        if (addSet != null) addSet.add(id);
    }
    private static void putItemSet(int id, InvType invType) {
        Set<Integer> addSet = null;
        switch (invType) {
            case SWORD:
                addSet = swordSet;
                break;
            case AXE:
                addSet = axeSet;
                break;
            case BOW:
                addSet = bowSet;
                break;
            case PASSIVEA:
                addSet = passiveASet;
                break;
            case PASSIVEB:
                addSet = passiveBSet;
                break;
            case PASSIVEC:
                addSet = passiveCSet;
                break;
        }
        if (addSet != null) addSet.add(id);
    }

    public static BiMap<Integer, Skill> getIdSkillMap() {
        return idSkillMap;
    }
    public static HashMap<Integer, BookFormatter> getIdUserReadMap() {
        return idUserReadMap;
    }
    public static Set<Integer> getBruteSet() {
        bruteSet.addAll(getGlobalSet());
        return bruteSet;
    }
    public static Set<Integer> getKnightSet() {
        knightSet.addAll(getGlobalSet());
        return knightSet;
    }
    public static Set<Integer> getRangerSet() {
        rangerSet.addAll(getGlobalSet());
        return rangerSet;
    }
    public static Set<Integer> getMageSet() {
        mageSet.addAll(getGlobalSet());
        return mageSet;
    }
    public static Set<Integer> getAssassinSet() {
        assassinSet.addAll(getGlobalSet());
        return assassinSet;
    }
    public static Set<Integer> getGlobalSet() {
        return globalSet;
    }

    public static Set<Integer> getSwordSet() {
        return swordSet;
    }
    public static Set<Integer> getAxeSet() {
        return axeSet;
    }
    public static Set<Integer> getBowSet() {
        return bowSet;
    }
    public static Set<Integer> getPassiveASet() {
        return passiveASet;
    }
    public static Set<Integer> getPassiveBSet() {
        return passiveBSet;
    }
    public static Set<Integer> getPassiveCSet() {
        return passiveCSet;
    }

    //Lookups vvvvv

    public static int getSkillId(String name) {
        for (Map.Entry<Integer, Skill> skillEntry : idSkillMap.entrySet()) {
            if (name.toLowerCase().contains(skillEntry.getValue().getName().toLowerCase())) return skillEntry.getKey();
        }
        return -1;
    }
    public static Skill getSkillById(int id) {
        return idSkillMap.get(id);
    }
    public static BookFormatter getSkillFormatter(ItemStack book) {
        String test = ChatColor.stripColor(book.getItemMeta().getDisplayName());
        int b = InventoryData.getSkillId(test);
        return (b != -1) ? InventoryData.getIdUserReadMap().get(b) : null;
    }
    public static Skill getSkill(ItemStack book) throws NullPointerException {
        BookFormatter bf = getSkillFormatter(book);
        return bf.getSkill();
    }

    public static BookFormatter getSkillFormatter(Skill skill) {
        for(int i : idSkillMap.keySet()) {
            Skill skillz = idSkillMap.get(i);
            if(skillz.getName().equalsIgnoreCase(skill.getName())) {
                return idUserReadMap.get(i);
            }
        }
        throw new NullPointerException("skill is not defined");
    }
}
