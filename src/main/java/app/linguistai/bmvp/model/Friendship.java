package app.linguistai.bmvp.model;

import java.time.LocalDateTime;

import app.linguistai.bmvp.model.embedded.FriendshipId;
import app.linguistai.bmvp.model.enums.FriendshipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "friendship")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FriendshipId.class)
public class Friendship {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1", referencedColumnName = "id", insertable = false, updatable = false)
    private User user1;

    @Id
    @ManyToOne(fetch = FetchType.LAZY) // if the fetch type is eager than delete operation does not work TODO check if smth doesnt work
    @JoinColumn(name = "user2", referencedColumnName = "id", nullable = false)
    private User user2;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @NotNull
    @Column(name = "status", nullable = false)
    private FriendshipStatus status;
}