package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class StreakException extends CustomException {
    public StreakException() {
        super("Oops! Something went wrong with streaks!", HttpStatus.BAD_REQUEST);
    }

    public StreakException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
}
