package app.linguistai.bmvp.model.wordbank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import app.linguistai.bmvp.model.embedded.WordSelectionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "word_selection")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(WordSelectionId.class)
public class WordSelection {
    @Id
    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "unknown_word_list", referencedColumnName = "unknown_word_list"),
        @JoinColumn(name = "word", referencedColumnName = "word")
    })
    private UnknownWord word;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
}