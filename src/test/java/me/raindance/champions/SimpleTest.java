package me.raindance.champions;

import me.raindance.champions.util.MathUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTest {

    @Test
    @DisplayName("Rounding Test")
    public void round() {
        System.out.println("test");
        assertEquals(100.12, MathUtil.round(100.12312321, 2));
    }
}
