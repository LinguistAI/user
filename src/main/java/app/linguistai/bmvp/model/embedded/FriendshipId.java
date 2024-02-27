package app.linguistai.bmvp.model.embedded;

import app.linguistai.bmvp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipId implements Serializable {
    private User user1;
    private User user2;
}
