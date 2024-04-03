package app.linguistai.bmvp.controller.gamification;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.request.gamification.QQuestSendMessage;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.gamification.quest.IQuestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("quest")
public class QuestController {
    private final IQuestService questService;

    @PostMapping(path = "/process/send-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processSendMessage(@RequestHeader(Header.USER_EMAIL) String email, @Valid @RequestBody QQuestSendMessage message) {
        try {
            questService.processSendMessage(email, message);
            return Response.create("Successfully fetched UserXP", HttpStatus.OK);
        }
        catch (Exception e1) {
            return Response.create(e1.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
