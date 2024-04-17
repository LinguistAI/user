package app.linguistai.bmvp.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RLeaderboardXP {
    private List<RUserXPRanking> XPRankings; // All users' rankings
    private RUserXPRanking loggedUserXPRanking; // Logged user's ranking
    private int totalPages;
    private int currentPage;
}
