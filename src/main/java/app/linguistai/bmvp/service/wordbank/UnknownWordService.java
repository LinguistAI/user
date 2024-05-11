package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.AlreadyFoundException;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.UnauthorizedException;
import app.linguistai.bmvp.exception.WordReferencedException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UnknownWordId;
import app.linguistai.bmvp.enums.ConfidenceEnum;
import app.linguistai.bmvp.model.wordbank.ListStats;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.model.wordbank.UnknownWordListWithUser;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.model.wordbank.IConfidenceCount;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordListRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordRepository;
import app.linguistai.bmvp.repository.wordbank.IWordSelectionRepository;
import app.linguistai.bmvp.request.wordbank.QAddUnknownWord;
import app.linguistai.bmvp.request.wordbank.QPredefinedWordList;
import app.linguistai.bmvp.request.wordbank.QUnknownWordList;
import app.linguistai.bmvp.response.wordbank.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static app.linguistai.bmvp.consts.LanguageCodes.CODE_ENGLISH;
import static app.linguistai.bmvp.utils.FileUtils.readPredefinedWordListFromYamlFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class UnknownWordService implements IUnknownWordService {
    private final IUnknownWordRepository wordRepository;

    private final IUnknownWordListRepository listRepository;

    private final IAccountRepository accountRepository;

    private final IWordSelectionRepository wordSelectionRepository;

    private final int MODIFY_LIST_ACTIVE = 1001;

    private final int MODIFY_LIST_FAVORITE = 1002;

    private final int MODIFY_LIST_PINNED = 1003;

    private final int MODIFY_WORD_CONFIDENCE_UP = 2001;

    private final int MODIFY_WORD_CONFIDENCE_DOWN = 2002;

    private WebClient dictionaryWebClient;
    private final WebClient.Builder dictionaryWebClientBuilder;

    @Value("${dict.service.base.url}")
    private String DICT_SERVICE_BASE_URL;

    private WebClient getDictionaryWebClient() {
        if (dictionaryWebClient == null) {
            dictionaryWebClient = dictionaryWebClientBuilder.baseUrl(DICT_SERVICE_BASE_URL).build();
        }
        return dictionaryWebClient;
    }

    @Override
    @Transactional
    public RUnknownWordLists getListsByEmail(String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            // Get current language of user
            String languageOfUser = user.getCurrentLanguage();

            List<UnknownWordList> listsOfUser = listRepository.findByUserIdOrderByIsPinnedDesc(user.getId());
            List<RUnknownWordList> responseListsOfUser = new ArrayList<>();

            for (UnknownWordList list : listsOfUser) {
                // If the language of the list is not the same as users current language, skip
                if (list.getLanguage() == null || !list.getLanguage().equalsIgnoreCase(languageOfUser)) {
                    continue;
                }

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
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (Exception e1) {
            log.error("Get lists by email failed for email {}", email, e1);
            throw new SomethingWentWrongException();
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
                .map(word -> new RUnknownWord(word.getWord().toLowerCase(), word.getConfidence()))
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
            log.error("Could not get unknown word lists.");
            throw new SomethingWentWrongException();
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
                .language(user.getCurrentLanguage())
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
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (Exception e1) {
            log.error("Could not create unknown word list.");
            throw new SomethingWentWrongException();
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
                .language(user.getCurrentLanguage())
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
            log.error("Could not edit unknown word list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public RUnknownWord addWord(QAddUnknownWord qAddUnknownWord, String email, boolean allowUnknown) throws Exception {
        try {
            // Check if dictionary service base URL is set
            if (DICT_SERVICE_BASE_URL == null || DICT_SERVICE_BASE_URL.isEmpty()) {
                throw new IllegalStateException("Dict Service Base URL is not set. Word addition is not allowed.");
            }

            // Retrieve user's word list
            UnknownWordListWithUser userListInfo = this.getUserOwnedList(qAddUnknownWord.getListId(), email);
            UnknownWordList userList = userListInfo.list();

            // Check if the same word already exists
            UnknownWordId unknownWordId = UnknownWordId.builder()
                .ownerList(userList)
                .word(qAddUnknownWord.getWord().toLowerCase())
                .build();

            wordRepository.findById(unknownWordId).ifPresent(existingWord -> {
                throw new RuntimeException("Word " + qAddUnknownWord.getWord().toLowerCase() + " already exists in list " + userList.getTitle() + ".");
            });

            // Call dictionary service only if allowUnknown is false
            if (!allowUnknown) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("wordList", Collections.singletonList(qAddUnknownWord.getWord().toLowerCase()));

                this.getDictionaryWebClient().post()
                        .header(Header.USER_EMAIL, email)
                        .body(Mono.just(requestBody), Map.class)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            }

            // Create and save new unknown word
            UnknownWord newWord = UnknownWord.builder()
                .ownerList(userList)
                .word(qAddUnknownWord.getWord().toLowerCase())
                .confidence(ConfidenceEnum.LOWEST)
                .build();

            UnknownWord saved = wordRepository.save(newWord);

            return RUnknownWord.builder().word(saved.getWord().toLowerCase()).confidence(saved.getConfidence()).build();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.error("Word {} is not found in the dictionary.", qAddUnknownWord.getWord());
                throw new NotFoundException(String.format("It looks like %s isn't in our dictionary yet. Please double-check your spelling.", qAddUnknownWord.getWord()));
            }
            log.error("Dictionary service returned an error:" + e.getMessage());
            throw new SomethingWentWrongException("We had trouble communicating with our dictionary service, please try again later.");
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new AlreadyFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Could not add unknown word: " + e.getMessage());
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public RUnknownWord increaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception {
        try {
            return modifyWord(qAddUnknownWord.getListId(), email, qAddUnknownWord.getWord().toLowerCase(), MODIFY_WORD_CONFIDENCE_UP);
        }
        catch (Exception e1) {
            log.error("Could not increase word confidence.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public RUnknownWord decreaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception {
        try {
            return modifyWord(qAddUnknownWord.getListId(), email, qAddUnknownWord.getWord().toLowerCase(), MODIFY_WORD_CONFIDENCE_DOWN);
        }
        catch (Exception e1) {
            log.error("Could not decrease word confidence.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public ROwnerUnknownWordList activateList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.TRUE, MODIFY_LIST_ACTIVE);
        }
        catch (Exception e1) {
            log.error("Could not activate list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public ROwnerUnknownWordList deactivateList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.FALSE, MODIFY_LIST_ACTIVE);
        }
        catch (Exception e1) {
            log.error("Could not deactivate list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public ROwnerUnknownWordList addFavoriteList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.TRUE, MODIFY_LIST_FAVORITE);
        }
        catch (Exception e1) {
            log.error("Could not add list to favorites.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public ROwnerUnknownWordList removeFavoriteList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.FALSE, MODIFY_LIST_FAVORITE);
        }
        catch (Exception e1) {
            log.error("Could not remove list from favorites.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public ROwnerUnknownWordList pinList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.TRUE, MODIFY_LIST_PINNED);
        }
        catch (Exception e1) {
            log.error("Could not pin list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public ROwnerUnknownWordList unpinList(UUID listId, String email) throws Exception {
        try {
            return modifyList(listId, email, Boolean.FALSE, MODIFY_LIST_PINNED);
        }
        catch (Exception e1) {
            log.error("Could not unpin list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public ROwnerUnknownWordList deleteList(UUID listId, String email) throws Exception {
        try {
            UnknownWordListWithUser userListInfo = this.getUserOwnedList(listId, email);
            UnknownWordList userList = userListInfo.list();

            // Check if any words in the list are referenced in WordSelection
            List<UnknownWord> wordsInList = wordRepository.findByOwnerListListId(listId);
            boolean isAnyWordReferenced = wordsInList.stream().anyMatch(wordSelectionRepository::existsByWord);

            // If any word is referenced, throw an error and revert changes
            if (isAnyWordReferenced) {
                throw new WordReferencedException("Cannot delete this list because it contains words that are active in a conversation. Please deactivate the list and try again later.");
            }

            // Delete all words associated with the list
            wordRepository.deleteByOwnerListListId(listId);

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

            // If we are here, the list exists, the user is the owner of the list, and no word is referenced
            listRepository.deleteById(listId);

            return response;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("When deleting a list, user is not found for email {}.", email);
            } else if (e.getObject().equals(UnknownWordList.class.getSimpleName())) {
                log.error("When deleting a list, word list {} not found.", listId);
            } else {
                log.error("When deleting a list, something went wrong.", e);
            }
            throw e;
        } catch (UnauthorizedException e) {
            log.error("User {} not authorized to modify list: {}", email, listId);
            throw e;
        } catch (WordReferencedException e) {
            log.error("Cannot delete list '{}' because it contains words referenced in WordSelection", listId);
            throw e;
        } catch (Exception e) {
            log.error("Could not delete list {} for user with email {}.", listId, email, e);
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public RUnknownWord deleteWord(UUID listId, String email, String word) throws Exception {
        try {
            // Check if the user can modify this word list
            this.getUserOwnedList(listId, email);

            // Get the word
            UnknownWord unknownWord = wordRepository.findByOwnerListListIdAndWord(listId, word)
                    .orElseThrow(() -> new NotFoundException(UnknownWord.class.getSimpleName(), true));

            // Check if the word is referenced in WordSelection
            boolean isReferenced = wordSelectionRepository.existsByWord(unknownWord);
            if (isReferenced) {
                throw new WordReferencedException(String.format("Cannot delete '%s' because it is an active word in a conversation.", word));
            }

            RUnknownWord response = RUnknownWord.builder()
                    .word(unknownWord.getWord())
                    .confidence(unknownWord.getConfidence()).build();

            // If we are here, the list exists, the user is the owner of the list, and the word is not referenced
            wordRepository.deleteByOwnerListListIdAndWord(listId, word);
            return response;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("When deleting a word, user is not found for email {}", email);
            } else if (e.getObject().equals(UnknownWordList.class.getSimpleName())) {
                log.error("When deleting a word, word list {} not found ", listId);
            } else {
                log.error("When deleting a word, word {} is not found for user email {} and list id {}", word, email, listId);
            }
            throw e;
        } catch (UnauthorizedException e) {
            log.error("User {} not authorized to modify list: {}", email, listId);
            throw e;
        } catch (WordReferencedException e) {
            log.error("Cannot delete word '{}' since it is referenced in the WordSelection table (active words)", word);
            throw e;
        } catch (Exception e) {
            log.error("Could not delete word {} for user with email {}.", word, email, e);
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public UnknownWordList getRandomActiveUnknownWordList(UUID userId) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist"));

            List<UnknownWordList> activeLists = listRepository.findByUserId(userId)
                .stream()
                .filter(UnknownWordList::getIsActive)
                .filter(list -> user.getCurrentLanguage() != null && user.getCurrentLanguage().equalsIgnoreCase(list.getLanguage()))
                .toList();

            if (activeLists.isEmpty()) {
                throw new NotFoundException();
            }

            return activeLists.get(new Random().nextInt(activeLists.size()));
        }
        catch (NotFoundException e) {
            log.error("No active unknown word lists found for user with ID, either because list does not exist or user does not exist {}", userId);
            throw e;
        }
        catch (Exception e) {
            log.error("Could not retrieve random active unknown word list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public void ensureUserListsHaveLanguage(String email) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist"));

            List<UnknownWordList> listsWithoutLanguage = listRepository.findByUserId(user.getId())
                .stream()
                // The filter below checks if the user has a current language, and if the list does not have a language
                .filter(list -> user.getCurrentLanguage() != null && (list.getLanguage() == null || list.getLanguage().isBlank()))
                .toList();


            // Update the language of each list to CODE_ENGLISH
            listsWithoutLanguage.forEach(list -> list.setLanguage(CODE_ENGLISH));

            // Save the updated lists back to the repository
            listRepository.saveAll(listsWithoutLanguage);
        }
        catch (NotFoundException e) {
            log.error("User does not exist for email {}", email);
            throw e;
        }
        catch (Exception e) {
            log.error("Could not retrieve random active unknown word list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public String getRandomWordFromList(UUID listId) throws Exception {
        try {
            List<UnknownWord> words = wordRepository.findByOwnerListListId(listId);

            if (words.isEmpty()) {
                throw new NotFoundException();
            }

            return words.get(new Random().nextInt(words.size())).getWord().toLowerCase();
        }
        catch (NotFoundException e) {
            log.error("No words found in the unknown word list with ID {}", listId);
            throw e;
        }
        catch (Exception e) {
            log.error("Could not retrieve a random word from the list.");
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public List<String> getRandomWordFromList(UUID listId, Integer numOfWords) throws Exception {
        try {
            List<UnknownWord> words = wordRepository.findByOwnerListListId(listId);

            if (words.isEmpty()) {
                throw new NotFoundException();
            }

            if (words.size() < numOfWords) {
                throw new IllegalArgumentException("Number of requested random words are greater than the available number of words.");
            }

            Collections.shuffle(words);
            return words.subList(0, numOfWords).stream().map(UnknownWord::getWord).map(String::toLowerCase).collect(Collectors.toList());
        }
        catch (NotFoundException e) {
            log.error("No words found in the unknown word list with ID {}", listId);
            throw e;
        }
        catch (Exception e) {
            log.error("Could not retrieve a random word from the list.");
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public ROwnerUnknownWordList addPredefinedWordList(String wordListYamlFile, String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            QPredefinedWordList predefinedWordList = readPredefinedWordListFromYamlFile(wordListYamlFile);

            // Create new unknown word list
            UnknownWordList wordList = UnknownWordList.builder()
                .listId(UUID.randomUUID())
                .user(user)
                .title(predefinedWordList.getTitle())
                .description(predefinedWordList.getDescription())
                .isActive(predefinedWordList.getIsActive())
                .isFavorite(predefinedWordList.getIsFavorite())
                .isPinned(predefinedWordList.getIsPinned())
                .imageUrl(predefinedWordList.getImageUrl())
                .language(CODE_ENGLISH)
                .build();

            UnknownWordList savedList = listRepository.save(wordList);

            // Add predefined words
            List<UnknownWord> unknownWords = new ArrayList<>();
            for (String word : predefinedWordList.getWords()) {
                UnknownWord newWord = UnknownWord.builder()
                    .ownerList(savedList)
                    .word(word.toLowerCase())
                    .confidence(ConfidenceEnum.LOWEST)
                    .build();

                unknownWords.add(newWord);
            }

            // Bulk insert all words
            wordRepository.saveAll(unknownWords);

            return ROwnerUnknownWordList.builder()
                    .listId(wordList.getListId())
                    .ownerUsername(user.getUsername())
                    .title(wordList.getTitle())
                    .description(wordList.getDescription())
                    .isActive(wordList.getIsActive())
                    .isFavorite(wordList.getIsFavorite())
                    .isPinned(wordList.getIsPinned())
                    .imageUrl(wordList.getImageUrl())
                    .listStats(this.getListStats(wordList.getListId()))
                    .build();

        } catch (Exception e) {
            log.error("Could not add predefined word list.");
            throw new SomethingWentWrongException();
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
            .orElseThrow(() -> new NotFoundException(User.class.getSimpleName()));

        // Check if list exists
        UnknownWordList userList = listRepository.findById(listId)
            .orElseThrow(() -> new NotFoundException(UnknownWordList.class.getSimpleName()));

        // Check if user is owner of the list
        if (userList.getUser().getId() != user.getId()) {
            throw new UnauthorizedException("User not authorized to modify list.");
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
    public RUnknownWordListsStats getAllListStatsByEmail(String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            return getAllListStats(user);
        }
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (Exception e1) {
            log.error("Get list stats by email failed for email {}", email, e1);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RUnknownWordListsStats getAllListStatsByUserId(UUID userId) throws Exception {
        try {
            User user = accountRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist for given userId: [" + userId + "]."));

            return getAllListStats(user);
        }
        catch (NotFoundException e) {
            log.error("User is not found for userId {}", userId);
            throw e;
        }
        catch (Exception e1) {
            log.error("Get list stats by userId failed for userId {}", userId, e1);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    private RUnknownWordListsStats getAllListStats(User user) throws Exception {
        try {
            ListStats stats = new ListStats(0L, 0L, 0L);

            // Get word counts by confidence level
            List<IConfidenceCount> wordCountsByConfidenceLevel = wordRepository.countWordsByConfidenceLevel(user.getId());

            // Update list stats based on confidence levels
            for (IConfidenceCount confidenceCount : wordCountsByConfidenceLevel) {
                ConfidenceEnum confidence = confidenceCount.getConfidence();
                Long count = confidenceCount.getCount();

                stats = updateStatsBasedOnConfidence(stats, confidence, count);
            }

            log.info("Retrieved list stats for user {}.", user.getEmail());
            return RUnknownWordListsStats.builder()
                    .listStats(stats)
                    .build();
        } catch (Exception e) {
            log.error("Failed to retrieve list stats for user {}.", user.getEmail(), e);
            throw new SomethingWentWrongException();
        }
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
