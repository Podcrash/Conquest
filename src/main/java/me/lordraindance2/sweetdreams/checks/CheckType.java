package me.lordraindance2.sweetdreams.checks;

import java.util.Arrays;

public enum CheckType {
    CLICKS, NOSLOWDOWN, SPEEDUP, TEST, MISPLACE, REACH, NULL;

    public String getName() {
        return this.name();
    }

    public static CheckType getByName(String name) {
        for(CheckType checkType : values()) {
            if(name.equalsIgnoreCase(checkType.name()))
                return checkType;
        }
        return CheckType.NULL;
    }
}
