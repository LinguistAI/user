package app.linguistai.bmvp.model.gamification.quest;

import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import app.linguistai.bmvp.utils.QuestCompletionCriteriaConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@Builder
@Table(name = "quest")
@NoArgsConstructor
@AllArgsConstructor
public class Quest {
    @Id
    @NotNull
    @Column(name = "quest_id", nullable = false)
    private Long questId;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;

    @NotBlank
    @Column(name = "type", nullable = false)
    private String type;

    @NotBlank
    @Column(name = "image", nullable = false)
    private String image;

    @NotNull
    @Column(name = "reward", nullable = false)
    private Long reward;

    @NotNull
    @Convert(converter = QuestCompletionCriteriaConverter.class)
    @Column(name = "completion_criteria", nullable = false, columnDefinition = "TEXT")
    private QuestCompletionCriteria completionCriteria;
}

