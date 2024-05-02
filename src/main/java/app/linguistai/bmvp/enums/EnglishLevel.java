package app.linguistai.bmvp.enums;

import java.util.HashMap;
import java.util.Map;

public enum EnglishLevel {
    DONT_KNOW("DONT_KNOW"),
    BEGINNER("BEGINNER"),
    INTERMEDIATE("INTERMEDIATE"),
    ADVANCED("ADVANCED"),
    NATIVE("NATIVE");

    private static final Map<String, EnglishLevel> LEVEL_MAP = new HashMap<>();
    static {
        for (EnglishLevel level : EnglishLevel.values()) {
            LEVEL_MAP.put(level.levelString, level);
        }
    }

    private final String levelString;

    private EnglishLevel(String levelString) {
        this.levelString = levelString;
    }

    public int getLevel() {
        return this.ordinal();
    }

    public String getLevelString() {
        return levelString;
    }

    public static EnglishLevel getLevel(int level) {
        for (EnglishLevel englishLevel : EnglishLevel.values()) {
            if (englishLevel.ordinal() == level) {
                return englishLevel;
            }
        }
        return EnglishLevel.DONT_KNOW; // default if level not found
    }

    public static EnglishLevel fromStringOrDefault(String levelString) {
        return LEVEL_MAP.getOrDefault(levelString, DONT_KNOW);
    }
}