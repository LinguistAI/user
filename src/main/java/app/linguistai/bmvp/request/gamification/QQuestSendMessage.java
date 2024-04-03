package app.linguistai.bmvp.request.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QQuestSendMessage extends QQuestTypeAction {
    @NotBlank
    private String message;

    public QQuestSendMessage(
        @JsonProperty("message") String message
    ) {
        this.message = message;
    }
}
