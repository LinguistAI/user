package app.linguistai.bmvp.model.gamification.store;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UserItemId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "user_item")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserItemId.class)
public class UserItem {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storeItem", referencedColumnName = "id", nullable = false)
    private StoreItem storeItem;

    @Column(name = "quantity")
    private int quantity;
}