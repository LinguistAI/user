package app.linguistai.bmvp.controller;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.ConversationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Deprecated
@AllArgsConstructor
@RestController
@RequestMapping("conversation")
public class ConversationController {
    private final ConversationService conversationService;
    
    @GetMapping
    public ResponseEntity<Object> getConversationByUser(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully fetched Conversation", HttpStatus.OK, conversationService.getConversationByUserEmail(email));
        }
        catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
