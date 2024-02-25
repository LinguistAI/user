package app.linguistai.bmvp.controller.gamification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.gamification.UserStreakService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("user-streak")
public class UserStreakController {
    private final UserStreakService userStreakService;

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllUserStreaks() {
        try {
            return Response.create("Successfully fetched all UserStreaks", HttpStatus.OK, userStreakService.getAllUserStreaks());
        }
        catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserStreakByToken(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully fetched UserStreak", HttpStatus.OK, userStreakService.getUserStreakByToken(email));
        }
        catch (NotFoundException e1) {
            return Response.create("UserStreak does not exist for user email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e2) {
            return Response.create(ExceptionLogger.log(e2), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createUserStreak(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully created UserStreak", HttpStatus.OK, userStreakService.createUserStreak(email));
        }
        catch (Exception e1) {
            return Response.create("Could not create UserStreak", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
