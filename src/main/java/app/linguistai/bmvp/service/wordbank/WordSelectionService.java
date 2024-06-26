package app.linguistai.bmvp.service.wordbank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordRepository;
import app.linguistai.bmvp.repository.wordbank.IWordSelectionRepository;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.ConfidenceEnum;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import app.linguistai.bmvp.model.wordbank.WordSelection;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.request.QSelectWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WordSelectionService {

    private final IWordSelectionRepository wordSelectionRepository;
    private final IAccountRepository accountRepository;
    private final IUnknownWordRepository unknownWordRepository;

    public List<WordSelection> getSelectedWords(QSelectWord selectWord, String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));


            List<String> preservedWords = new ArrayList<>(selectWord.getPreservedWords());

            // Get the previously selected words
            List<WordSelection> selectedWords = 
                                wordSelectionRepository
                                .findByConversationIdAndWordOwnerListUserEmail(
                                    selectWord.getConversationId(), 
                                    user.getEmail());


            // Remove the words that are not in the preservedWords list from the selectedWords list
            selectedWords.removeIf(wordSelection -> !preservedWords.contains(wordSelection.getWord().getWord()));

            // Remove all words
            wordSelectionRepository.deleteAll(selectedWords);

            // If the size of the selectedWords list is less than the required size, select new words
            if (selectedWords.size() < selectWord.getSize()) {
                List<UnknownWord> selectedUnknownWords = this.selectWords(user.getId(), selectWord.getSize() - selectedWords.size());

                // Iterate over each selected word and create a WordSelection object
                for (UnknownWord unknownWord : selectedUnknownWords) {
                    WordSelection wordSelection = WordSelection.builder()
                            .conversationId(selectWord.getConversationId())
                            .word(unknownWord)
                            .build();

                    selectedWords.add(wordSelection);
                }

                // Save all the words back to db
                wordSelectionRepository.saveAll(selectedWords);

                log.info("New words are selected for user email {}", email);
            }

            log.info("Selected words are retrieved for user email {}", email);

            return selectedWords;
        } catch (NotFoundException e) {         
            log.error("User does not exist for email {}", email, e);
            throw e;
        } catch (Exception e2) {         
            log.error("Word selection failed for email {}", email, e2);
            throw new SomethingWentWrongException();
        }
    }

    private List<UnknownWord> selectWords(UUID userId, int selectSize) throws Exception {
        try {
            int initialSelectSize = selectSize;

            List<UnknownWord> selectedWords;
            List<ConfidenceEnum> confidences = new ArrayList<>(Arrays.asList(ConfidenceEnum.LOWEST, ConfidenceEnum.LOW));

            // Select words whose confidences are lowest or low
            selectedWords = unknownWordRepository.findRandomByOwnerListUserIdAndOwnerListIsActiveAndConfidence(
                userId, true, confidences, selectSize);

            if (selectedWords.size() == initialSelectSize) {
                return selectedWords;
            }

            // Select words whose confidences are moderate
            selectSize = initialSelectSize - selectedWords.size();
            confidences.clear();
            confidences.add(ConfidenceEnum.MODERATE);

            selectedWords.addAll(unknownWordRepository.findRandomByOwnerListUserIdAndOwnerListIsActiveAndConfidence(
                userId, true, confidences, selectSize));

            if (selectedWords.size() == initialSelectSize) {
                return selectedWords;
            }

            // Select words whose confidences are high or highest
            selectSize = initialSelectSize - selectedWords.size();
            confidences.clear();
            confidences.addAll(Arrays.asList(ConfidenceEnum.HIGH, ConfidenceEnum.HIGHEST));
            
            selectedWords.addAll(unknownWordRepository.findRandomByOwnerListUserIdAndOwnerListIsActiveAndConfidence(
                userId, true, confidences, selectSize));

            if (selectedWords.size() != initialSelectSize) {
                log.error("Error in selecting new words, selected words size is not equal to initial select size, " +
                        "selectedWords size: {}, initialSelectSize: {}", selectedWords.size(), initialSelectSize);
                throw new NotFoundException("Error in selecting new words, you need to add more unknown words to your word bank");
            }

            return selectedWords;
        } catch (NotFoundException e) {
            log.error("Error in selecting new words, you need to add more unknown words to your word bank", e);
            throw e;
        } catch (Exception e) {
            log.error("Error in selecting new words", e);
            throw new SomethingWentWrongException();
        }
    }
}
