package me.raindance.champions.kits.classes.annotation;

import me.raindance.champions.kits.enums.SkillType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseChampion {
    SkillType skillType();
}
