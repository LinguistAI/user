package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {
    private String object = "";

    public NotFoundException() {
        super("NotFoundException", HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    
    public NotFoundException(String obj, String message) {
        super(message, HttpStatus.NOT_FOUND);
        this.object = obj;
    }
    
    public NotFoundException(String obj, boolean isObject) {
        super(String.format("%s is not found", obj), HttpStatus.NOT_FOUND);
        this.object = obj;
    }

    public NotFoundException(String obj, String key, String value) {
        super(String.format("%s with %s: %s is not found", obj), HttpStatus.NOT_FOUND);
        this.object = obj;
    }

    public String getObject() {
        return object;
    }
}
