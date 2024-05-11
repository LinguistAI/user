package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.request.wordbank.QAddUnknownWord;
import app.linguistai.bmvp.request.wordbank.QUnknownWordList;
import app.linguistai.bmvp.response.wordbank.ROwnerUnknownWordList;
import app.linguistai.bmvp.response.wordbank.RUnknownWord;
import app.linguistai.bmvp.response.wordbank.RUnknownWordListWords;
import app.linguistai.bmvp.response.wordbank.RUnknownWordLists;
import app.linguistai.bmvp.response.wordbank.RUnknownWordListsStats;

import java.util.List;
import java.util.UUID;

public interface IUnknownWordService {
    RUnknownWordLists getListsByEmail(String email) throws Exception;
    RUnknownWordListWords getListWithWordsById(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList createList(QUnknownWordList qUnknownWordList, String email) throws Exception;
    ROwnerUnknownWordList editList(UUID listId, QUnknownWordList qUnknownWordList, String email) throws Exception;
    RUnknownWord addWord(QAddUnknownWord qAddUnknownWord, String email, boolean allowUnknown) throws Exception;
    RUnknownWord increaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    RUnknownWord decreaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    ROwnerUnknownWordList activateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList deactivateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList addFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList removeFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList pinList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList unpinList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList deleteList(UUID listId, String email) throws Exception;
    RUnknownWord deleteWord(UUID listId, String email, String word) throws Exception;
    UnknownWordList getRandomActiveUnknownWordList(UUID userId) throws Exception;
    String getRandomWordFromList(UUID listId) throws Exception;
    List<String> getRandomWordFromList(UUID listId, Integer numOfWords) throws Exception;
    ROwnerUnknownWordList addPredefinedWordList(String wordListYamlFile, String email) throws Exception;
    RUnknownWordListsStats getAllListStatsByEmail(String email) throws Exception;
    RUnknownWordListsStats getAllListStatsByUserId(UUID userId) throws Exception;
    void ensureUserListsHaveLanguage(String email) throws Exception;
}
