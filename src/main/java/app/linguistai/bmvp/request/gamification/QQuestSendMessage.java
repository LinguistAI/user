package app.linguistai.bmvp.request.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QQuestSendMessage {
    @NotBlank
    private String message;

    public QQuestSendMessage(
        @JsonProperty("message") String message
    ) {
        this.message = message;
    }
}
