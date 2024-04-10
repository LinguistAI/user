package app.linguistai.bmvp.model.currency;

import app.linguistai.bmvp.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_gems")
@NoArgsConstructor
public class UserGems {
    @Id
    @NotNull
    private UUID userId; // maps to User.id by @MapsId

    @OneToOne(fetch = FetchType.LAZY)
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
    @Column(name = "gems", nullable = false)
    private Long gems;
}
