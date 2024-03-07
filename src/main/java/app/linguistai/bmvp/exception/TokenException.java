package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class TokenException extends CustomException {
    public TokenException() {
        super("Oops! Something went wrong with authorization!", HttpStatus.BAD_REQUEST);
    }

    public TokenException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
}
