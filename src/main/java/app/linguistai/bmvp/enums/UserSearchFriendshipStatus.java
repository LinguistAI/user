package app.linguistai.bmvp.enums;

// Note: If this values are updated, 
// the status values in the IAccountRepository must be updated as well
public enum UserSearchFriendshipStatus {
    FRIEND(0),
    REQUEST_SENT(1),
    REQUEST_RECEIVED(2),
    NOT_EXIST(3);

    private final int value;

    UserSearchFriendshipStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserSearchFriendshipStatus fromValue(int value) {
        for (UserSearchFriendshipStatus status : UserSearchFriendshipStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}