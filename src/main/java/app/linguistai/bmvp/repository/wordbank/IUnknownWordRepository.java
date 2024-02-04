
package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.wordbank.UnknownWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUnknownWordRepository extends JpaRepository<UnknownWord, UUID> {

}
