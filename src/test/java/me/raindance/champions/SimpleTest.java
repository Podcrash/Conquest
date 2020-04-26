package me.raindance.champions;

import com.podcrash.api.util.MathUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTest extends LoggableTest {

    @Test
    @DisplayName("Rounding Test")
    public void round() {
        assertEquals(100.12, MathUtil.round(100.12312321, 2));
    }
}
