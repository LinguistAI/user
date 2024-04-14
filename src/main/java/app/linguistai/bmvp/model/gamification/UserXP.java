package app.linguistai.bmvp.model.gamification;

import app.linguistai.bmvp.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_xp")
@NoArgsConstructor
public class UserXP {
    @Id
    @NotNull
    private UUID userId; // maps to User.id by @MapsId

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(),
        nullable = false
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @NotNull
    @Column(name = "experience", nullable = false)
    private Long experience;
}
