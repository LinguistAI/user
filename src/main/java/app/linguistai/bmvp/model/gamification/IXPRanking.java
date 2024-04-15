package app.linguistai.bmvp.model.gamification;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface IXPRanking {
    Long getRanking();
    byte[] getUserIdAsByte();
    Long getExperience();

    default UUID getUserIdAsUUID() {
        // Convert Byte array id to UUID
        byte[] byteArray = getUserIdAsByte();
        if (byteArray != null) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            // UUID is represented as two long integers
            long high = byteBuffer.getLong();
            long low = byteBuffer.getLong();
            return new UUID(high, low);
        }
        return null;
    }
}
