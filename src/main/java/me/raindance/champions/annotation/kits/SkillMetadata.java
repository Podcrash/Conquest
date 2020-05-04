package me.raindance.champions.annotation.kits;

import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.SkillType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SkillMetadata {
    int id();
    SkillType skillType() default SkillType.Global;
    InvType invType() default InvType.SWORD;
    double cost() default 1000D;
}
