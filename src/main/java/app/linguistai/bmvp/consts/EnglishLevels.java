package app.linguistai.bmvp.consts;

public enum EnglishLevels {
    DONT_KNOW(0, "Don't Knowww"),
    BEGINNER(1, "Beginner"),
    INTERMEDIATE(2, "Intermediate"),
    ADVANCED(3, "Advanced"),
    NATIVE(4, "Native"),
    UNKNOWN(-1, "Unknown");

    private final int level;
    private final String levelString;

    EnglishLevels(int level, String levelString) {
        this.level = level;
        this.levelString = levelString;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelString() {
        return levelString;
    }

    public static EnglishLevels fromInt(int level) {
        for (EnglishLevels englishLevel : EnglishLevels.values()) {
            if (englishLevel.level == level) {
                return englishLevel;
            }
        }
        return UNKNOWN;
    }
}
