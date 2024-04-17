package app.linguistai.bmvp.controller.gamification;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.model.gamification.quest.Quest;
import app.linguistai.bmvp.request.gamification.QQuestSendMessage;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.gamification.quest.IQuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Process send message", description = "Processes send message event for Quests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully processed send message event"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Process send message for quests failed")
    })
    @PostMapping(path = "/process/send-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processSendMessage(
        @RequestHeader(Header.USER_EMAIL) String email,
        @Valid @RequestBody QQuestSendMessage message) throws Exception
    {
        questService.processSendMessage(email, message);
        return Response.create("Successfully processed send message event", HttpStatus.OK);
    }

    @Operation(summary = "Get all quests of user", description = "Returns all active quests of user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved quests",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Quest.class, type = "array"))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Get user quests failed")
    })
    @GetMapping
    public ResponseEntity<Object> getUserQuests(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully retrived user quests", HttpStatus.OK, questService.getUserQuests(email));
    }
}
