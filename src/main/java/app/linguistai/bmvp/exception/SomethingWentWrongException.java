package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class SomethingWentWrongException extends CustomException {
    public SomethingWentWrongException() {
        super("Oops! Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SomethingWentWrongException(String msg) {
        super(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
