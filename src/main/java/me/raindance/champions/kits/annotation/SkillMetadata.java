package me.raindance.champions.kits.annotation;

import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SkillMetadata {
    SkillType skillType() default SkillType.Global;
    InvType invType() default InvType.SWORD;
}
