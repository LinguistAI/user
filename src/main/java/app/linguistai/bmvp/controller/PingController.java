package app.linguistai.bmvp.controller;

import app.linguistai.bmvp.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("ping")
public class PingController {

    @GetMapping
    public ResponseEntity<Object> ping() {
        return Response.create("Server is up", HttpStatus.OK);
    }
}
