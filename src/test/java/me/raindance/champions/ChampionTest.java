package me.raindance.champions;

import com.podcrash.api.redis.Communicator;
import me.raindance.champions.inventory.MenuCreator;
import me.raindance.champions.kits.SkillInfo;
import me.raindance.champions.kits.enums.SkillType;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ChampionTest {
    @BeforeAll
    public static void before() {
        try {
            Communicator.setup(Executors.newSingleThreadExecutor()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        SkillInfo.setUp();

        System.out.println(SkillInfo.getSkills(SkillType.Warden));

    }

    @Test
    @DisplayName("Warden test")
    public void isHeroIn() {
        SkillType type = SkillInfo.getSkills(SkillType.Warden).get(0).getSkillType();
        Assertions.assertEquals(type, SkillType.Warden);
    }
}
