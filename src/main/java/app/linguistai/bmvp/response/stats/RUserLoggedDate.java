package app.linguistai.bmvp.response.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.sql.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RUserLoggedDate {
	private List<Date> loggedDates;
}
