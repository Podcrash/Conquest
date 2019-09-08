package me.raindance.champions;

import me.raindance.champions.util.MathUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnotherTest extends LoggableTest {
    @Test
    @DisplayName("Another Rounding Test")
    public void round() {
        assertEquals(2323.12, MathUtil.round(2323.124124321, 2));
    }
}
