package app.linguistai.bmvp.controller;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.request.QMessage;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.IMessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Deprecated
@AllArgsConstructor
@RestController
@RequestMapping("message")
public class MessageController {
    @Autowired
    @Qualifier("no-llm-message-service")
    private final IMessageService messageService;

    @PostMapping
    public ResponseEntity<Object> sendMessage(@Valid @RequestBody QMessage qMessage, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully sent Message", HttpStatus.OK, messageService.sendMessage(qMessage, email));
        }
        catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
