package app.linguistai.bmvp.exception.store;

import app.linguistai.bmvp.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InsufficientUserItemQuantityException extends CustomException {
    public InsufficientUserItemQuantityException() {
        super("Not enough items", HttpStatus.BAD_REQUEST);
    }
}
