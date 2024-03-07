package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class LoginException extends CustomException {
    public LoginException() {
        super("Username or password is wrong!", HttpStatus.NOT_FOUND);
    }
}
