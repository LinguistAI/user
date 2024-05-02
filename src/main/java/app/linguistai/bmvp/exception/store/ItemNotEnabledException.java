package app.linguistai.bmvp.exception.store;

import app.linguistai.bmvp.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ItemNotEnabledException extends CustomException {
    public ItemNotEnabledException() {
        super("Store item is not enabled", HttpStatus.BAD_REQUEST);
    }
}
