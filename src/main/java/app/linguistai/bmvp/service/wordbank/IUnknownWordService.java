package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.request.wordbank.QAddUnknownWord;
import app.linguistai.bmvp.request.wordbank.QUnknownWordList;
import app.linguistai.bmvp.response.wordbank.ROwnerUnknownWordList;
import app.linguistai.bmvp.response.wordbank.RUnknownWord;
import app.linguistai.bmvp.response.wordbank.RUnknownWordListWords;
import app.linguistai.bmvp.response.wordbank.RUnknownWordLists;
import app.linguistai.bmvp.response.wordbank.RUnknownWordListsStats;

import java.util.UUID;

public interface IUnknownWordService {
    RUnknownWordLists getListsByEmail(String email) throws Exception;
    RUnknownWordListWords getListWithWordsById(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList createList(QUnknownWordList qUnknownWordList, String email) throws Exception;
    ROwnerUnknownWordList editList(UUID listId, QUnknownWordList qUnknownWordList, String email) throws Exception;
    RUnknownWord addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    RUnknownWord increaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    RUnknownWord decreaseConfidence(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    ROwnerUnknownWordList activateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList deactivateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList addFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList removeFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList pinList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList unpinList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList deleteList(UUID listId, String email) throws Exception;
    RUnknownWordListsStats getAllListStats(String email) throws Exception;
}
