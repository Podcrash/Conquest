package me.raindance.champions.kits.annotation;


import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ItemMetaData {
    Material mat();
    SkillType skillType() default SkillType.Global;


}
