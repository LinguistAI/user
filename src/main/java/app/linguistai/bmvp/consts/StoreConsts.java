package app.linguistai.bmvp.consts;

public class StoreConsts {
    // Types
    public static final String TYPE_DOUBLE_ANSWER = "Double Answer";
    public static final String TYPE_ELIMINATE_WRONG_ANSWER = "Eliminate Wrong Answer";
    public static final String[] QUIZ_TYPES = {TYPE_DOUBLE_ANSWER, TYPE_ELIMINATE_WRONG_ANSWER};

    // Descriptions
    public static final String DESCRIPTION_DOUBLE_ANSWER = "Consume this item to have two attempts for a quiz question.";
    public static final String DESCRIPTION_ELIMINATE_WRONG_ANSWER = "Consume this item to eliminate two wrong answers from a quiz question.";

    // Prices
    public static final Long PRICE_DOUBLE_ANSWER = 100L;
    public static final Long PRICE_ELIMINATE_WRONG_ANSWER = 50L;

    // Enabled status
    public static final boolean ENABLED_DOUBLE_ANSWER = true;
    public static final boolean ENABLED_ELIMINATE_WRONG_ANSWER = true;
}
