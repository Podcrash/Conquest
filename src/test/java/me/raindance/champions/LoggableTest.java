package me.raindance.champions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggableTest {
    private Logger logger;

    protected Logger getLogger() {
        if(logger == null) logger = Logger.getLogger(getClass().getSimpleName());
        return logger;
    }
    protected void log(String text) {
        getLogger().info(text);
    }
    protected void log(Level level, String text) {
        getLogger().log(level, text);
    }
}
