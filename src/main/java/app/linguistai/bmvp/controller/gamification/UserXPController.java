package app.linguistai.bmvp.controller.gamification;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.gamification.IXPService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("user-xp")
public class UserXPController {
    private final IXPService xpService;

    @GetMapping
    public ResponseEntity<Object> getUserXPByEmail(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.getUserXP(email));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> incrementUserXPLoginTest(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.LOGIN));
    }

    @PostMapping("/streak")
    public ResponseEntity<Object> incrementUserXPStreakTest(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully fetched UserXP", HttpStatus.OK, xpService.increaseUserXP(email, XPAction.STREAK));
    }
}
