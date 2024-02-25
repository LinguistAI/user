package app.linguistai.bmvp.model.enums;

public enum XPAction {
    LOGIN("login"),
    STREAK("streak");

    private final String key;

    XPAction(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
