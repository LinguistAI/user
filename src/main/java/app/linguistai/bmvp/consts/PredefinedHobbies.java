package app.linguistai.bmvp.consts;

public class PredefinedHobbies {
    public static final int DONT_KNOW = 0;
    public static final int BEGINNER = 1;
    public static final int INTERMEDIATE = 2;
    public static final int ADVANCED = 3;
    public static final int NATIVE = 4;

    public static String getLevelString(int level) {
        switch (level) {
            case BEGINNER:
                return "Beginner";
            case INTERMEDIATE:
                return "Intermediate";
            case ADVANCED:
                return "Advanced";
            case NATIVE:
                return "Native";
            case DONT_KNOW:
                return "Don't Know";
            default:
                return "Unknown";
        }
    }
}
