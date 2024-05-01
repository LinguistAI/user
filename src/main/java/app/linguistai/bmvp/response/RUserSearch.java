package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.UserSearchFriendshipStatus;

@Data
@Builder
@AllArgsConstructor
public class RUserSearch {
    private UUID id;
    private String username;
    private String email;
    private UserSearchFriendshipStatus friendshipStatus;

    public RUserSearch(User user, UserSearchFriendshipStatus status) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.friendshipStatus = status;
    }
}
