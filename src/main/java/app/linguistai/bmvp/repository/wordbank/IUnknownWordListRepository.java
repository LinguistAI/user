package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUnknownWordListRepository extends JpaRepository<UnknownWordList, UUID> {

}
