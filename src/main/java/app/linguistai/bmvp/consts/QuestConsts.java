package app.linguistai.bmvp.consts;

public class QuestConsts {
    // General Identifiers
    public static final String TIMES_IDENTIFIER = "<TIMES>";
    public static final String TIMES_CLOSING_IDENTIFIER = "</TIMES>";

    // Quest "Times" Upper Limits
    public static final int USE_WORD_TIMES_UPPER_LIMIT = 8;
    public static final int SEND_MESSAGE_TIMES_UPPER_LIMIT = 10;

    // Quest "Times" Lower Limits
    public static final int USE_WORD_TIMES_LOWER_LIMIT = 3;
    public static final int SEND_MESSAGE_TIMES_LOWER_LIMIT = 6;

    // Use Word Quest Amount
    public static final int ASSIGN_USE_WORD_QUEST_AMOUNT = 2;

    // Types
    public static final String TYPE_USE_WORD = "WORD";
    public static final String TYPE_SEND_MESSAGE = "MESSAGE";
    public static final String TYPE_ADD_WORD_TO_LIST = "ADD_WORD";

    // Placeholder Images
    public static final String QUEST_WORD_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/0/08/Microsoft_Word_logo_%282013-2019%29.png";
    public static final String QUEST_MESSAGE_IMAGE = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJguvlwi4aggqQVErF52ET4Hr2NUFruDZm0w&usqp=CAU";
    public static final String QUEST_ADD_WORD_IMAGE = "https://cdn-icons-png.freepik.com/256/6462/6462989.png";

    // Rewards
    public static final Long BASE_USE_WORD_REWARD = 15L;
    public static final Long BASE_SEND_MESSAGE_REWARD = 10L;
    public static final Long ADD_WORD_REWARD = 20L;

    // Use Word Type Quest Consts
    public static final String WORD_IDENTIFIER = "<WORD>";
    public static final String WORD_CLOSING_IDENTIFIER = "</WORD>";
    public static final String WORD_PRACTICE_TITLE = "Word Practice";
    public static final String WORD_PRACTICE_DESC = "Use the word '" + WORD_IDENTIFIER + "' " + TIMES_IDENTIFIER + " times.";

    // Send Message Type Quest Consts
    public static final String SEND_MESSAGE_TITLE = "Conversation";
    public static final String SEND_MESSAGE_DESC = "Send " + TIMES_IDENTIFIER + " messages.";
}
