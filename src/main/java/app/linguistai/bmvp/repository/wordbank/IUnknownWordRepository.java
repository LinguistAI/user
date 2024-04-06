package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.model.wordbank.IConfidenceCount;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUnknownWordRepository extends JpaRepository<UnknownWord, UnknownWordId> {
    List<UnknownWord> findByOwnerListListId(UUID listId);
    Optional<UnknownWord> findByOwnerListListIdAndWord(UUID listId, String word);

    @Query("SELECT u.confidence AS confidence, COUNT(u) AS count FROM UnknownWord u WHERE u.ownerList.user.id = :userId GROUP BY u.confidence")
    List<IConfidenceCount> countWordsByConfidenceLevel(@Param("userId") UUID userId);
}
