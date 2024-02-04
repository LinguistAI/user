package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.Confidence;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordListRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordRepository;
import app.linguistai.bmvp.request.QAddUnknownWord;
import app.linguistai.bmvp.request.QCreateUnknownWordList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UnknownWordService implements IUnknownWordService {
    private final IUnknownWordRepository wordRepository;

    private final IUnknownWordListRepository listRepository;

    private final IAccountRepository accountRepository;

    @Override
    public UnknownWordList createList(QCreateUnknownWordList qCreateUnknownWordList, String email) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            // Build new unknown word list
            UnknownWordList newList = UnknownWordList.builder()
                .listId(UUID.randomUUID())
                .user(user)
                .title(qCreateUnknownWordList.getTitle())
                .description(qCreateUnknownWordList.getDescription())
                .isActive(qCreateUnknownWordList.getIsActive())
                .isFavorite(qCreateUnknownWordList.getIsFavorite())
                .build();

            return listRepository.save(newList);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not add unknown word.");
            throw e1;
        }
    }

    @Override
    public void addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            // Check if list exists
            UnknownWordList userList = listRepository.findById(qAddUnknownWord.getListId())
                    .orElseThrow(() -> new NotFoundException("Unknown Word List does not exist for given listId: [" + qAddUnknownWord.getListId() + "]."));

            // Check if user is owner of the list
            if (userList.getUser().getId() != user.getId()) {
                throw new Exception("User not authorized to add new word to list.");
            }

            // Build new unknown word
            UnknownWord newWord = UnknownWord.builder()
                    .ownerList(userList)
                    .word(qAddUnknownWord.getWord())
                    .confidence(Confidence.LOWEST)
                    .build();

            wordRepository.save(newWord);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not add unknown word.");
            throw e1;
        }
    }
}
