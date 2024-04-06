package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.model.enums.ConfidenceEnum;
import app.linguistai.bmvp.model.wordbank.ListStats;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.model.wordbank.UnknownWordListWithUser;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.model.wordbank.IConfidenceCount;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordListRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordRepository;
import app.linguistai.bmvp.request.wordbank.QAddUnknownWord;
import app.linguistai.bmvp.request.wordbank.QUnknownWordList;
import app.linguistai.bmvp.response.wordbank.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UnknownWordService implements IUnknownWordService {
    private final IUnknownWordRepository wordRepository;

    private final IUnknownWordListRepository listRepository;

    private final IAccountRepository accountRepository;

    private final int MODIFY_LIST_ACTIVE = 1001;

    private final int MODIFY_LIST_FAVORITE = 1002;

    private final int MODIFY_LIST_PINNED = 1003;

    private final int MODIFY_WORD_CONFIDENCE_UP = 2001;

    private final int MODIFY_WORD_CONFIDENCE_DOWN = 2002;

    @Override
    @Transactional
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
                    .isPinned(list.getIsPinned())
                    .imageUrl(list.getImageUrl())
                    .listStats(this.getListStats(list.getListId()))
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
    @Transactional
    public RUnknownWordListWords getListWithWordsById(UUID listId, String email) throws Exception {
        try {
            UnknownWordListWithUser userListInfo = this.getUserOwnedList(listId, email);
            User user = userListInfo.user();
            UnknownWordList userList = userListInfo.list();

            List<RUnknownWord> responseWords = wordRepository.findByOwnerListListId(listId)
                .stream()
                .map(word -> new RUnknownWord(word.getWord(), word.getConfidence()))
                .collect(Collectors.toList());

            return RUnknownWordListWords.builder()
                .unknownWordList(ROwnerUnknownWordList.builder()
                    .listId(userList.getListId())
                    .ownerUsername(user.getUsername())
                    .title(userList.getTitle())
                    .description(userList.getDescription())
                    .isActive(userList.getIsActive())
                    .isFavorite(userList.getIsFavorite())
                    .isPinned(userList.getIsPinned())
                    .imageUrl(userList.getImageUrl())
                    .listStats(this.getListStats(userList.getListId()))
                    .build())
                .words(responseWords)
                .build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not get unknown word lists.");
            throw e1;
        }
    }

    @Override
    @Transactional
    public ROwnerUnknownWordList createList(QUnknownWordList qUnknownWordList, String email) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            // Build new unknown word list
            UnknownWordList newList = UnknownWordList.builder()
                .listId(UUID.randomUUID())
                .user(user)
                .title(qUnknownWordList.getTitle())
                .description(qUnknownWordList.getDescription())
                .isActive(qUnknownWordList.getIsActive())
                .isFavorite(qUnknownWordList.getIsFavorite())
                .isPinned(qUnknownWordList.getIsPinned())
                .imageUrl(qUnknownWordList.getImageUrl())
                .build();

            UnknownWordList savedList = listRepository.save(newList);

            return ROwnerUnknownWordList.builder()
                .listId(savedList.getListId())
                .ownerUsername(user.getUsername())
                .title(savedList.getTitle())
                .description(savedList.getDescription())
                .isActive(savedList.getIsActive())
                .isFavorite(savedList.getIsFavorite())
                .isPinned(savedList.getIsPinned())
                .imageUrl(savedList.getImageUrl())
                .listStats(this.getListStats(savedList.getListId()))
                .build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not create unknown word list.");
            throw e1;
        }
    }

    @Override
    @Transactional
    public ROwnerUnknownWordList editList(UUID listId, QUnknownWordList qUnknownWordList, String email) throws Exception {
        try {
            UnknownWordListWithUser userListInfo = this.getUserOwnedList(listId, email);
            User user = userListInfo.user();
            UnknownWordList userList = userListInfo.list();

            // Build edited unknown word list
            UnknownWordList editedList = UnknownWordList.builder()
                .listId(userList.getListId())
                .user(user)
                .title(qUnknownWordList.getTitle())
                .description(qUnknownWordList.getDescription())
                .isActive(qUnknownWordList.getIsActive())
                .isFavorite(qUnknownWordList.getIsFavorite())
                .isPinned(qUnknownWordList.getIsPinned())
                .imageUrl(qUnknownWordList.getImageUrl())
                .build();

            UnknownWordList savedList = listRepository.save(editedList);

            return ROwnerUnknownWordList.builder()
                .listId(savedList.getListId())
                .ownerUsername(user.getUsername())
                .title(savedList.getTitle())
                .description(savedList.getDescription())
                .isActive(savedList.getIsActive())
                .isFavorite(savedList.getIsFavorite())
                .isPinned(savedList.getIsPinned())
                .imageUrl(savedList.getImageUrl())
                .listStats(this.getListStats(savedList.getListId()))
                .build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not edit unknown word list.");
            throw e1;
        }
    }

    @Override
    @Transactional
    public RUnknownWord addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception {
        try {
            UnknownWordListWithUser userListInfo = this.getUserOwnedList(qAddUnknownWord.getListId(), email);
            UnknownWordList userList = userListInfo.list();

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
                .confidence(ConfidenceEnum.LOWEST)
                .build();

            UnknownWord saved = wordRepository.save(newWord);

            return RUnknownWord.builder().word(saved.getWord()).confidence(saved.getConfidence()).build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not add unknown word.");
            throw e1;
        }
    }

    @Override
    public RUnknownWord increaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception {
        try {
            return modifyWord(qAddUnknownWord.getListId(), email, qAddUnknownWord.getWord(), MODIFY_WORD_CONFIDENCE_UP);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not increase word confidence.");
            throw e1;
        }
    }

    @Override
    public RUnknownWord decreaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception {
        try {
            return modifyWord(qAddUnknownWord.getListId(), email, qAddUnknownWord.getWord(), MODIFY_WORD_CONFIDENCE_DOWN);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not decrease word confidence.");
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

    @Override
    public ROwnerUnknownWordList pinList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.TRUE, MODIFY_LIST_PINNED);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not pin list.");
            throw e1;
        }
    }

    @Override
    public ROwnerUnknownWordList unpinList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.FALSE, MODIFY_LIST_PINNED);
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not unpin list.");
            throw e1;
        }
    }

    @Override
    @Transactional
    public ROwnerUnknownWordList deleteList(UUID listId, String email) throws Exception {
        try {
            UnknownWordListWithUser userListInfo = this.getUserOwnedList(listId, email);
            UnknownWordList userList = userListInfo.list();

            ROwnerUnknownWordList response = ROwnerUnknownWordList.builder()
                .listId(userList.getListId())
                .ownerUsername(userListInfo.user().getUsername())
                .title(userList.getTitle())
                .description(userList.getDescription())
                .isActive(userList.getIsActive())
                .isFavorite(userList.getIsFavorite())
                .isPinned(userList.getIsPinned())
                .imageUrl(userList.getImageUrl())
                .listStats(this.getListStats(userList.getListId()))
                .build();

            // If we are here, the list exists and the user is the owner of the list
            listRepository.deleteById(listId);

            return response;
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not delete list.");
            throw e1;
        }
    }

    @Transactional
    protected RUnknownWord modifyWord(UUID listId, String email, String word, int mode) throws Exception {
        if (mode != MODIFY_WORD_CONFIDENCE_UP && mode != MODIFY_WORD_CONFIDENCE_DOWN) {
            throw new Exception("Invalid modification attempt for Unknown Word.");
        }

        UnknownWordListWithUser userListInfo = this.getUserOwnedList(listId, email);

        // If we are here, user is authorized to edit list and words within the list
        UnknownWord wordToEdit = wordRepository.findByOwnerListListIdAndWord(userListInfo.list().getListId(), word)
            .orElseThrow(() -> new NotFoundException("Unknown Word [" + word + "] does not exist for given listId: [" + listId + "]."));

        switch (mode) {
            case MODIFY_WORD_CONFIDENCE_UP -> wordToEdit.setConfidence(this.increaseConfidence(wordToEdit.getConfidence()));
            case MODIFY_WORD_CONFIDENCE_DOWN -> wordToEdit.setConfidence(this.decreaseConfidence(wordToEdit.getConfidence()));
        }

        UnknownWord updated = wordRepository.save(wordToEdit);

        return RUnknownWord.builder().word(updated.getWord()).confidence(updated.getConfidence()).build();
    }

    @Transactional
    protected ROwnerUnknownWordList modifyList(UUID listId, String email, Boolean newValue, int mode) throws Exception {
        if (mode != MODIFY_LIST_ACTIVE && mode != MODIFY_LIST_FAVORITE && mode != MODIFY_LIST_PINNED) {
            throw new Exception("Invalid modification attempt for Unknown Word List.");
        }

        UnknownWordListWithUser userListInfo = this.getUserOwnedList(listId, email);
        User user = userListInfo.user();
        UnknownWordList userList = userListInfo.list();

        // If we are here, user is authorized to edit list
        switch (mode) {
            case MODIFY_LIST_ACTIVE -> userList.setIsActive(newValue);
            case MODIFY_LIST_FAVORITE -> userList.setIsFavorite(newValue);
            case MODIFY_LIST_PINNED -> userList.setIsPinned(newValue);
        }

        // If we are here, we know we are trying to modify isActive, isFavorite or isPinned
        // therefore we don't need to check if userList has changed
        UnknownWordList updated = listRepository.save(userList);

        return ROwnerUnknownWordList.builder()
            .listId(updated.getListId())
            .ownerUsername(user.getUsername())
            .title(updated.getTitle())
            .description(updated.getDescription())
            .isActive(updated.getIsActive())
            .isFavorite(updated.getIsFavorite())
            .isPinned(updated.getIsPinned())
            .imageUrl(updated.getImageUrl())
            .listStats(this.getListStats(updated.getListId()))
            .build();
    }

    @Transactional
    protected UnknownWordListWithUser getUserOwnedList(UUID listId, String email) throws Exception {
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

        return new UnknownWordListWithUser(user, userList);
    }

    @Transactional
    protected ListStats getListStats(UUID listId) throws Exception {
        // Check if list exists
        listRepository.findById(listId)
            .orElseThrow(() -> new NotFoundException("Unknown Word List does not exist for given listId: [" + listId + "]."));

        ListStats stats = new ListStats(0L, 0L, 0L);

        List<UnknownWord> words = wordRepository.findByOwnerListListId(listId);

        for (UnknownWord word : words) {
            stats = updateStatsBasedOnConfidence(stats, word.getConfidence(), 1L);
        }

        return stats;
    }

    @Transactional
    public RUnknownWordListsStats getAllListStats(String email) throws Exception {
        User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        ListStats stats = new ListStats(0L, 0L, 0L);

        // Get word counts by confidence level
        List<IConfidenceCount> wordCountsByConfidenceLevel = wordRepository.countWordsByConfidenceLevel(user.getId());

        // Update list stats based on confidence levels
        for (IConfidenceCount confidenceCount : wordCountsByConfidenceLevel) {
            ConfidenceEnum confidence = confidenceCount.getConfidence();
            Long count = confidenceCount.getCount();

            stats = updateStatsBasedOnConfidence(stats, confidence, count);
        }

        return RUnknownWordListsStats.builder()
                .listStats(stats)
                .build();
    }

    private ConfidenceEnum increaseConfidence(ConfidenceEnum currentConfidence) {
        return (currentConfidence.ordinal() < ConfidenceEnum.values().length - 1)
            ? ConfidenceEnum.values()[currentConfidence.ordinal() + 1]
            : currentConfidence;
    }

    private ConfidenceEnum decreaseConfidence(ConfidenceEnum currentConfidence) {
        return (currentConfidence.ordinal() > 0)
            ? ConfidenceEnum.values()[currentConfidence.ordinal() - 1]
            : currentConfidence;
    }

    private ListStats updateStatsBasedOnConfidence(ListStats stats, ConfidenceEnum confidence, Long count) {
        switch (confidence) {
            // Learning
            case LOWEST, LOW -> stats.setLearning(stats.getLearning() + count);

            // Reviewing
            case MODERATE, HIGH -> stats.setReviewing(stats.getReviewing() + count);

            // Mastered
            case HIGHEST -> stats.setMastered(stats.getMastered() + count);
        }

        return stats;
    }
}
