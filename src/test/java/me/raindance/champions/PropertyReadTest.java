package me.raindance.champions;

import com.podcrash.api.mc.util.MathUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.podcrash.api.mc.util.ConfigUtil.readPropertiesFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PropertyReadTest extends LoggableTest {
    @Test
    @DisplayName("Reading allowed.properties test")
    public void readFile() {
        Properties prop = readPropertiesFile(getClass().getClassLoader(), "allowed.properties");
        assertNotNull(prop);

        Object rawObj = prop.get("allowed");
        assertNotNull(rawObj);

        Assertions.assertTrue(rawObj instanceof String);
        System.out.println(rawObj);
    }
}
