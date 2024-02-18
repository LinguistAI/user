package app.linguistai.bmvp.model;

import java.util.UUID;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_streak")
@NoArgsConstructor
public class UserStreak {
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
    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak;

    @NotNull
    @Column(name = "highest_streak", nullable = false)
    private Integer highestStreak;

    @NotNull
    @Column(name = "last_login", nullable = false)
    private Date lastLogin;
}
