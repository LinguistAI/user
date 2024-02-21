
package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUnknownWordRepository extends JpaRepository<UnknownWord, UnknownWordId> {
    List<UnknownWord> findByOwnerListListId(UUID listId);
    Optional<UnknownWord> findByOwnerListListIdAndWord(UUID listId, String word);
}
