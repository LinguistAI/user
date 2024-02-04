package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.request.QAddUnknownWord;
import app.linguistai.bmvp.request.QCreateUnknownWordList;

public interface IUnknownWordService {
    UnknownWordList createList(QCreateUnknownWordList qCreateUnknownWordList, String email) throws Exception;
    void addWord(QAddUnknownWord qAddUnknownWord, String email) throws Exception;
}
