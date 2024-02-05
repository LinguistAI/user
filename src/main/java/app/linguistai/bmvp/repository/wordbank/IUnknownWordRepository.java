
package app.linguistai.bmvp.repository.wordbank;

import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUnknownWordRepository extends JpaRepository<UnknownWord, UnknownWordId> {

}
