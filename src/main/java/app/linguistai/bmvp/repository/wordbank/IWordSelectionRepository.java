package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.embedded.WordSelectionId;
import app.linguistai.bmvp.model.wordbank.WordSelection;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IWordSelectionRepository extends JpaRepository<WordSelection, WordSelectionId> {
    List<WordSelection> findByConversationIdAndWordOwnerListUserEmail(UUID conversationId, String email);
}
