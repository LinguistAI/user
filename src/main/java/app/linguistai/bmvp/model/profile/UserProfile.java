package app.linguistai.bmvp.model.profile;

import java.time.LocalDate;
import java.util.UUID;

import app.linguistai.bmvp.consts.EnglishLevels;
import app.linguistai.bmvp.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Builder
@Table(name = "user_profile")
@AllArgsConstructor
public class UserProfile {
    @Id
    @NotNull
    private UUID userId;

    @NotNull
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(),
            nullable = false
    )
    private User user;

    @NotBlank
    @Column(name = "name", nullable = true)
    private String name;

    @NotNull
    @Column(name = "birthdate", nullable = true)
    private LocalDate birhtDate;

    @NotNull
    @Column(name = "english_level", nullable = true)
    private Integer englishLevel;

    public UserProfile() {
        this.englishLevel = EnglishLevels.DONT_KNOW;
    }
}
