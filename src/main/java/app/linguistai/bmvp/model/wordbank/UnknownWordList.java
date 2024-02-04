package app.linguistai.bmvp.model.wordbank;

import app.linguistai.bmvp.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "unknown_word_list")
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordList {
    @NotNull
    @Id
    @Column(name = "list_id", nullable = false)
    private UUID listId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey())
    private User user;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite;
}