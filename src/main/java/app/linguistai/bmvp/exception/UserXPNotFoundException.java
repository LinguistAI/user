package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class UserXPNotFoundException extends CustomException {
    public UserXPNotFoundException() {
        super("NotFoundException", HttpStatus.NOT_FOUND);
    }

    public UserXPNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
