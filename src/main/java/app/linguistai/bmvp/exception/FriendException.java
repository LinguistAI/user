package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class FriendException extends CustomException {
    public FriendException() {
        super("FriendException", HttpStatus.BAD_REQUEST);
    }

    public FriendException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
