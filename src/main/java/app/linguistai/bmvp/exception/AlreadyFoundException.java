package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFoundException extends CustomException {    
    public AlreadyFoundException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
    
    public AlreadyFoundException(String obj, boolean isObject) {
        super(String.format("%s already exists", obj), HttpStatus.BAD_REQUEST); 
    }
    
    // public AlreadyFoundException(String obj) {
    //     super(String.format("%s already exists", obj), HttpStatus.BAD_REQUEST);
    // }

    // public AlreadyFoundException(String obj, String key, String value) {
    //     super(String.format("%s with %s: %s already exists", obj), HttpStatus.BAD_REQUEST);
    // }
}
