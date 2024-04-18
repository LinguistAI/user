package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class InvalidResetCodeException extends CustomException {
    public InvalidResetCodeException() {
        super("Invalid reset code.", HttpStatus.BAD_REQUEST);
    }

    public InvalidResetCodeException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
}
