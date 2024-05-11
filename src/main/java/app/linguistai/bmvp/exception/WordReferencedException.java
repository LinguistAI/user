package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class WordReferencedException extends CustomException {
    public WordReferencedException() {
        super("This word is referenced elsewhere.", HttpStatus.BAD_REQUEST);
    }

    public WordReferencedException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
}
