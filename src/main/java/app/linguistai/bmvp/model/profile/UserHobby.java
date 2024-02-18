package app.linguistai.bmvp.model.profile;

import java.util.UUID;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UserHobbyId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_hobby")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserHobbyId.class)
public class UserHobby {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby", referencedColumnName = "id", nullable = false)
    private Hobby hobby;

    public UserHobby(UUID userId, Integer hobbyId) {
        this.user.setId(userId);
        this.hobby.setId(hobbyId);
    }
    
    public UserHobby(UUID userId, String hobbyName) {
        this.user.setId(userId);
        this.hobby.setName(hobbyName);
    }
}