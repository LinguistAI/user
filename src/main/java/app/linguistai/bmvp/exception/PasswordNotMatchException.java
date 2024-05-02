package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends CustomException {
    public PasswordNotMatchException() {
        super("Oops! Something went wrong!", HttpStatus.BAD_REQUEST);
    }
}
