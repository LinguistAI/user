package app.linguistai.bmvp.model.wordbank;

import app.linguistai.bmvp.enums.ConfidenceEnum;

public interface IConfidenceCount {
    ConfidenceEnum getConfidence();
    Long getCount();
}
