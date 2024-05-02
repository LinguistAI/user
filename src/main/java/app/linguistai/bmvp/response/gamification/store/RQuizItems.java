package app.linguistai.bmvp.response.gamification.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RQuizItems {
    private List<RUserItem> quizItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private Long gems;
}
