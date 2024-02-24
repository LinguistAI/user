package app.linguistai.bmvp.model.enums;

public enum FriendshipStatus {
    PENDING(0),
    ACCEPTED(1);

    private int value;

    FriendshipStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}