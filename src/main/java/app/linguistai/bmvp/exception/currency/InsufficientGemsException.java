package app.linguistai.bmvp.exception.currency;

import app.linguistai.bmvp.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InsufficientGemsException extends CustomException {
    public InsufficientGemsException() {
        super("Not enough gems", HttpStatus.BAD_REQUEST);
    }
}