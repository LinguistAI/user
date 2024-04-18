package app.linguistai.bmvp.model.wordbank;

import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.enums.ConfidenceEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "unknown_word")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UnknownWordId.class)
public class UnknownWord {
    @NotNull
    @Id
    @ManyToOne
    @JoinColumn(
            name = "unknown_word_list",
            referencedColumnName = "list_id",
            foreignKey = @ForeignKey(),
            nullable = false
    )
    private UnknownWordList ownerList;

    @NotBlank
    @Id
    private String word;

    @NotNull
    private ConfidenceEnum confidence;
}
