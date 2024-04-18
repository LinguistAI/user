package app.linguistai.bmvp.enums;

public enum TransactionType {
    SHOP_SPEND("shop_spend"), // for spending gems in the in-app store
    GEMS_PURCHASE("gems_purchase"), // for in-app purchase (support for potential future feature)
    GEMS_REWARD("gems_reward"); // for quests, achievements etc.

    private final String key;

    TransactionType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
