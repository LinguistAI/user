package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.enums.ConfidenceEnum;
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

    List<UnknownWord> findByOwnerListUserEmailAndOwnerListIsActiveAndConfidence(String email, Boolean isActive, ConfidenceEnum confidence);

    @Query(value = "SELECT u FROM UnknownWord u "
        + "WHERE u.ownerList.user.id = :userId "
        + "AND u.ownerList.isActive = :isActive "
        + "AND u.confidence IN :confidences "
        + "ORDER BY RAND() "
        + "LIMIT :limit")
    List<UnknownWord> findRandomByOwnerListUserIdAndOwnerListIsActiveAndConfidence(
            @Param("userId") UUID userId,
            @Param("isActive") Boolean isActive,
            @Param("confidences") List<ConfidenceEnum> confidences,
            @Param("limit") Integer limit
    );

    @Query("SELECT u.confidence AS confidence, COUNT(u) AS count FROM UnknownWord u WHERE u.ownerList.user.id = :userId GROUP BY u.confidence")
    List<IConfidenceCount> countWordsByConfidenceLevel(@Param("userId") UUID userId);

    void deleteByOwnerListListIdAndWord(UUID listId, String word);

    void deleteByOwnerListListId(UUID listId);
}
