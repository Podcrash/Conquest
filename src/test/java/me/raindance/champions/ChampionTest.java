package me.raindance.champions;

import com.podcrash.api.db.redis.Communicator;
import me.raindance.champions.kits.SkillInfo;
import me.raindance.champions.kits.enums.SkillType;
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
        Assertions.assertTrue(Communicator.isReady());
        Assertions.assertDoesNotThrow(SkillInfo::setUp);
    }

    @Test
    @DisplayName("Warden test")
    public void isHeroIn() {
        SkillType type = SkillInfo.getSkills(SkillType.Warden).get(0).getSkillType();
        Assertions.assertEquals(type, SkillType.Warden);
    }
}
