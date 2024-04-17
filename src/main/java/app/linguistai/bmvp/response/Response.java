package app.linguistai.bmvp.response;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class Response {

    public static ResponseEntity<Object> create(String message, HttpStatus status, Object data) {
        HashMap<String, Object> response = new HashMap<>();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("data", data);
        response.put("timestamp", timestamp);
        response.put("status", status.value());
        response.put("msg", message);        

        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<Object> create(String message, HttpStatus status) {
        HashMap<String, Object> response = new HashMap<>();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status.value());

        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<Object> create(String message, int status) {
        HashMap<String, Object> response = new HashMap<>();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status);

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(status));
    }
}