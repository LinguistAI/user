package app.linguistai.bmvp.controller.stats;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.response.stats.RUserLoggedDate;
import app.linguistai.bmvp.service.stats.UserLoggedDateService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("stats")
public class UserLoggedDateController {

	private final UserLoggedDateService loggedDateService;

	@GetMapping("/logged-date")
	public ResponseEntity<Object> getLoggedDate(@RequestHeader(Header.USER_EMAIL) String email,
	                                            @RequestParam(required = false, defaultValue = "desc") String sort,
	                                            @RequestParam(required = false) Integer daysLimit) {
		try {
			RUserLoggedDate loggedDates = loggedDateService.getLoggedDates(email, sort, daysLimit);
			return Response.create("Successfully fetched logged dates", HttpStatus.OK, loggedDates);
		} catch (Exception e) {
			return Response.create(ExceptionLogger.log(e), HttpStatus.BAD_REQUEST);
		}
	}

}
