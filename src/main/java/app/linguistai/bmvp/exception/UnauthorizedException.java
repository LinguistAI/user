package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException() {
        super("User is not authorized for this action.", HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String msg) {
        super(msg, HttpStatus.UNAUTHORIZED);
    }
}
