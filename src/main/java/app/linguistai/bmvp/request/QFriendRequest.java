package app.linguistai.bmvp.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QFriendRequest {
    @NotNull
    private UUID friendId;
}