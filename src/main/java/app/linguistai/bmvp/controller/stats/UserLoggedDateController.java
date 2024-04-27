package app.linguistai.bmvp.controller.stats;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.response.stats.RUserLoggedDate;
import app.linguistai.bmvp.response.wordbank.RUnknownWordListsStats;
import app.linguistai.bmvp.service.stats.UserLoggedDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("stats")
public class UserLoggedDateController {

    private final UserLoggedDateService loggedDateService;

    @Operation(summary = "Get Logged Date", description = "Retrieve logged dates for this user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched logged dates", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = RUserLoggedDate.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input parameter"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/logged-date")
    public ResponseEntity<Object> getLoggedDate(@RequestHeader(Header.USER_EMAIL) String email,
                                                @RequestParam(required = false, defaultValue = "desc") String sort,
                                                @RequestParam(required = false) Integer daysLimit) throws Exception {
        RUserLoggedDate loggedDates = loggedDateService.getLoggedDates(email, sort, daysLimit);
        return Response.create("Successfully fetched logged dates", HttpStatus.OK, loggedDates);
    }


    @Operation(summary = "Get Logged Date", description = "Retrieve logged dates for the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched logged dates", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = RUserLoggedDate.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input parameter"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/logged-date/{userId}")
    public ResponseEntity<Object> getLoggedDateAnotherUser(@RequestHeader(Header.USER_EMAIL) String email,
                                                           @Valid @PathVariable("userId") UUID userId,
                                                @RequestParam(required = false, defaultValue = "desc") String sort,
                                                @RequestParam(required = false) Integer daysLimit) throws Exception {
        RUserLoggedDate loggedDates = loggedDateService.getLoggedDates(userId, sort, daysLimit);
        return Response.create("Successfully fetched logged dates", HttpStatus.OK, loggedDates);
    }
}
