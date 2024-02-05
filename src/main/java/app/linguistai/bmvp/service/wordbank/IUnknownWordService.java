package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.request.QAddUnknownWord;
import app.linguistai.bmvp.request.QCreateUnknownWordList;
import app.linguistai.bmvp.response.ROwnerUnknownWordList;
import app.linguistai.bmvp.response.RUnknownWordLists;

import java.util.UUID;

public interface IUnknownWordService {
    RUnknownWordLists getListsByEmail(String email) throws Exception;
    ROwnerUnknownWordList createList(QCreateUnknownWordList qCreateUnknownWordList, String email) throws Exception;
    void addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
    ROwnerUnknownWordList activateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList deactivateList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList addFavoriteList(UUID listId, String email) throws Exception;
    ROwnerUnknownWordList removeFavoriteList(UUID listId, String email) throws Exception;
}
