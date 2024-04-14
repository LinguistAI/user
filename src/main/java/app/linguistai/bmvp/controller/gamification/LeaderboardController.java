package app.linguistai.bmvp.controller.gamification;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.response.gamification.RLeaderboardXP;
import app.linguistai.bmvp.service.gamification.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @Operation(summary = "Get Global XP Leaderboard", description = "Returns the global XP leaderboard. " +
            "Page numbers start from 0. If page param is not given, then currentPage is set to the page user is on." +
            "Rankings start from 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = RLeaderboardXP.class))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/global/xp")
    public ResponseEntity<Object> getXPLeaderboardGlobal(@RequestHeader(Header.USER_EMAIL) String email, @RequestParam(required = false) Integer page, @RequestParam(defaultValue = "10") int size) throws Exception {
        return Response.create("Successfully fetched global XP leaderboard", HttpStatus.OK, leaderboardService.getTopUsersByExperience(email, page, size));
    }

    @Operation(summary = "Get Friends XP Leaderboard", description = "Returns the XP leaderboard for friends. " +
            "Page numbers start from 0. If page param is not given, then currentPage is set to the page user is on." +
            "Rankings start from 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = RLeaderboardXP.class))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/friends/xp")
    public ResponseEntity<Object> getXPLeaderboardFriends(@RequestHeader(Header.USER_EMAIL) String email, @RequestParam(required = false) Integer page, @RequestParam(defaultValue = "10") int size) throws Exception {
        return Response.create("Successfully fetched friends XP leaderboard", HttpStatus.OK, leaderboardService.getTopFriendsByExperience(email, page, size));
    }
}
