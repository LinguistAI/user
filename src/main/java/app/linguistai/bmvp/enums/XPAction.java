package app.linguistai.bmvp.enums;

public enum XPAction {
    MESSAGE("message"),
    STREAK("streak"),
    ACHIEVEMENT("achievement"),
    QUEST("quest"),
    FRIEND("friend"),
    CELEBRATE("celebrate");

    private final String key;

    XPAction(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
