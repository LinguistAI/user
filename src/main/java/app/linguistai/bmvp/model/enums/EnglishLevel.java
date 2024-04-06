package app.linguistai.bmvp.model.enums;

public enum EnglishLevel {
    DONT_KNOW(0, "Don't Know"),
    BEGINNER(1, "Beginner"),
    INTERMEDIATE(2, "Intermediate"),
    ADVANCED(3, "Advanced"),
    NATIVE(4, "Native");

    private final int level;
    private final String levelString;

    private EnglishLevel(int level, String levelString) {
        this.level = level;
        this.levelString = levelString;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelString() {
        return levelString;
    }

    public static EnglishLevel getLevel(int level) {
        for (EnglishLevel englishLevel : EnglishLevel.values()) {
            if (englishLevel.level == level) {
                return englishLevel;
            }
        }
        return EnglishLevel.DONT_KNOW; // default if level not found
    }
}
