package app.linguistai.bmvp.model.gamification.quest.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "action")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UseWordCriteria.class, name = "use_word"),
        @JsonSubTypes.Type(value = SendMessageCriteria.class, name = "send_message"),
        @JsonSubTypes.Type(value = CreateWordListCriteria.class, name = "create_word_list")
})
public abstract class QuestCompletionCriteria {}
