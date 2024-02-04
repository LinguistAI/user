package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.request.QAddUnknownWord;
import app.linguistai.bmvp.request.QCreateUnknownWordList;
import app.linguistai.bmvp.response.RCreateNewUnknownWordList;

public interface IUnknownWordService {
    RCreateNewUnknownWordList createList(QCreateUnknownWordList qCreateUnknownWordList, String email) throws Exception;
    void addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
}
