package app.linguistai.bmvp.exception.currency;

import app.linguistai.bmvp.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidTransactionTypeException extends CustomException {
    public InvalidTransactionTypeException() {
        super("Unsupported transaction type", HttpStatus.BAD_REQUEST);
    }
}
