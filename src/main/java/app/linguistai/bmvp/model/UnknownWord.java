package app.linguistai.bmvp.model;

import app.linguistai.bmvp.model.embedded.UnknownWordId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "unknown_word")
@NoArgsConstructor
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
}
