package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IUnknownWordListRepository extends JpaRepository<UnknownWordList, UUID> {
    List<UnknownWordList> findByUserId(UUID userId);
    List<UnknownWordList> findByUserIdOrderByIsPinnedDesc(UUID userId);
}
