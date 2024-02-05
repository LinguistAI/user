package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.model.enums.Confidence;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordListRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordRepository;
import app.linguistai.bmvp.request.QAddUnknownWord;
import app.linguistai.bmvp.request.QCreateUnknownWordList;
import app.linguistai.bmvp.response.ROwnerUnknownWordList;
import app.linguistai.bmvp.response.RUnknownWordList;
import app.linguistai.bmvp.response.RUnknownWordLists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UnknownWordService implements IUnknownWordService {
    private final IUnknownWordRepository wordRepository;

    private final IUnknownWordListRepository listRepository;

    private final IAccountRepository accountRepository;

    private final int MODIFY_LIST_ACTIVE = 1001;

    private final int MODIFY_LIST_FAVORITE = 1002;

    @Override
    public RUnknownWordLists getListsByEmail(String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            List<UnknownWordList> listsOfUser = listRepository.findByUserId(user.getId());
            List<RUnknownWordList> responseListsOfUser = new ArrayList<>();

            for (UnknownWordList list : listsOfUser) {
                responseListsOfUser.add(RUnknownWordList.builder()
                    .listId(list.getListId())
                    .title(list.getTitle())
                    .description(list.getDescription())
                    .isActive(list.getIsActive())
                    .isFavorite(list.getIsFavorite())
                    .build()
                );
            }

            return RUnknownWordLists.builder()
                .ownerUsername(user.getUsername())
                .lists(responseListsOfUser)
                .build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not get unknown word lists.");
            throw e1;
        }
    }

    @Override
    public ROwnerUnknownWordList createList(QCreateUnknownWordList qCreateUnknownWordList, String email) throws Exception {
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

            UnknownWordList savedList = listRepository.save(newList);

            return ROwnerUnknownWordList.builder()
                .listId(savedList.getListId())
                .ownerUsername(user.getUsername())
                .title(savedList.getTitle())
                .description(savedList.getDescription())
                .isActive(savedList.getIsActive())
                .isFavorite(savedList.getIsFavorite())
                .build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not create unknown word list.");
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

            // Check if the same word already exists
            UnknownWordId unknownWordId = UnknownWordId.builder()
                .ownerList(userList)
                .word(qAddUnknownWord.getWord())
                .build();

            wordRepository.findById(unknownWordId).ifPresent(existingWord -> {
                throw new RuntimeException("Word " + qAddUnknownWord.getWord() + " already exists in list " + userList.getTitle() + ".");
            });

            // If the word does not exist in the list, build new unknown word
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

    @Override
    public ROwnerUnknownWordList activateList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.TRUE, MODIFY_LIST_ACTIVE);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not activate list.");
            throw e1;
        }
    }

    @Override
    public ROwnerUnknownWordList deactivateList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.FALSE, MODIFY_LIST_ACTIVE);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not deactivate list.");
            throw e1;
        }
    }

    @Override
    public ROwnerUnknownWordList addFavoriteList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.TRUE, MODIFY_LIST_FAVORITE);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not add list to favorites.");
            throw e1;
        }
    }

    @Override
    public ROwnerUnknownWordList removeFavoriteList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.FALSE, MODIFY_LIST_FAVORITE);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not remove list from favorites.");
            throw e1;
        }
    }

    private ROwnerUnknownWordList modifyList(UUID listId, String email, Boolean newValue, int mode) throws Exception {
        if (mode != MODIFY_LIST_ACTIVE && mode != MODIFY_LIST_FAVORITE) {
            throw new Exception("Invalid modification attempt for Unknown Word List.");
        }

        // Check if user exists
        User user = accountRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        // Check if list exists
        UnknownWordList userList = listRepository.findById(listId)
            .orElseThrow(() -> new NotFoundException("Unknown Word List does not exist for given listId: [" + listId + "]."));

        // Check if user is owner of the list
        if (userList.getUser().getId() != user.getId()) {
            throw new Exception("User not authorized to modify list.");
        }

        // If we are here, user is authorized to edit list
        switch (mode) {
            case MODIFY_LIST_ACTIVE -> userList.setIsActive(newValue);
            case MODIFY_LIST_FAVORITE -> userList.setIsFavorite(newValue);
        }

        // If we are here, we know we are trying to modify isActive or isFavorite
        // therefore we don't need to check if userList has changed
        UnknownWordList updated = listRepository.save(userList);

        return ROwnerUnknownWordList.builder()
            .listId(updated.getListId())
            .ownerUsername(user.getUsername())
            .title(updated.getTitle())
            .description(updated.getDescription())
            .isActive(updated.getIsActive())
            .isFavorite(updated.getIsFavorite())
            .build();
    }
}
