package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class RUserStreak {
    private UUID userId;
    private String username;
    private Integer currentStreak;
    private Integer highestStreak;
    private Date lastLogin;
}
