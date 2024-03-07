package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFoundException extends CustomException {    
    private String object = "";

    public AlreadyFoundException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
    
    public AlreadyFoundException(String obj, String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
        this.object = obj;
    }
    
    public AlreadyFoundException(String obj, boolean isObject) {
        super(String.format("%s already exists", obj), HttpStatus.BAD_REQUEST); 
        this.object = obj;
    }

    public String getObject() {
        return this.object;
    }
}
