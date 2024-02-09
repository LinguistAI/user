package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.request.wordbank.QAddUnknownWord;
import app.linguistai.bmvp.request.wordbank.QUnknownWordList;
import app.linguistai.bmvp.response.wordbank.ROwnerUnknownWordList;
import app.linguistai.bmvp.response.wordbank.RUnknownWordLists;

import java.util.UUID;

public interface IUnknownWordService {
    RUnknownWordLists getListsByEmail(String email) throws Exception;
    ROwnerUnknownWordList createList(QUnknownWordList qUnknownWordList, String email) throws Exception;
    ROwnerUnknownWordList editList(UUID listId, QUnknownWordList qUnknownWordList, String email) throws Exception;
    void addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    ROwnerUnknownWordList activateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList deactivateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList addFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList removeFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList pinList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList unpinList(UUID listId, String email) throws Exception;
}
