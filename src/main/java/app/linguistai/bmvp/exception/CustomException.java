package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {
    private HttpStatus status;

    public CustomException() {
        super("Something went wrong!");
        status = HttpStatus.BAD_REQUEST;
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public CustomException(String message, Throwable ex) {
        super(message, ex);
    }

    public CustomException(String message, HttpStatus status, Throwable ex) {
        super(message, ex);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
