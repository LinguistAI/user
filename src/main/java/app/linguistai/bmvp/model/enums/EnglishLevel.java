package app.linguistai.bmvp.model.enums;

public enum EnglishLevel {
    DONT_KNOW("Don't Know"),
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    NATIVE("Native");

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
}
