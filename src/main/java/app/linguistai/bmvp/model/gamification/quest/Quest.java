package app.linguistai.bmvp.model.gamification.quest;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import app.linguistai.bmvp.utils.QuestCompletionCriteriaConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;

@Data
@Entity
@Builder
@Table(name = "quest")
@NoArgsConstructor
@AllArgsConstructor
public class Quest {
    @Id
    @Column(name = "quest_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey())
    private User user;

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
    @Column(name = "assigned_date", nullable = false)
    private Date assignedDate;

    @NotNull
    @Convert(converter = QuestCompletionCriteriaConverter.class)
    @Column(name = "completion_criteria", nullable = false, columnDefinition = "TEXT")
    private QuestCompletionCriteria completionCriteria;
}

