package app.linguistai.bmvp.controller.gamification;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.enums.XPAction;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.gamification.IXPService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("user-xp")
public class UserXPController {
    private final IXPService xpService;

    @GetMapping
    public ResponseEntity<Object> getUserXPByEmail(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.getUserXPByEmail(email));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserXPByUserId(@RequestHeader(Header.USER_EMAIL) String email,
                                                    @Valid @PathVariable("userId") UUID userId) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.getUserXPById(userId));
    }

    @PostMapping("/message")
    public ResponseEntity<Object> incrementUserXPMessage(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.MESSAGE));
    }

    @PostMapping("/streak")
    public ResponseEntity<Object> incrementUserXPStreak(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.STREAK));
    }

    @PostMapping("/achievement")
    public ResponseEntity<Object> incrementUserXPAchievement(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.ACHIEVEMENT));
        }
        catch (Exception e1) {
            return Response.create(e1.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/quest")
    public ResponseEntity<Object> incrementUserXPQuest(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.QUEST));
        }
        catch (Exception e1) {
            return Response.create(e1.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/friend")
    public ResponseEntity<Object> incrementUserXPFriend(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.FRIEND));
        }
        catch (Exception e1) {
            return Response.create(e1.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/celebrate")
    public ResponseEntity<Object> incrementUserXPCelebrate(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.CELEBRATE));
        }
        catch (Exception e1) {
            return Response.create(e1.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
