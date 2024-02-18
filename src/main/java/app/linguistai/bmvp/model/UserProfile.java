package app.linguistai.bmvp.model;

import java.time.LocalDate;
import java.util.UUID;

import app.linguistai.bmvp.consts.EnglishLevels;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_profile")
@AllArgsConstructor
public class UserProfile {
    @Id
    @Column(name = "id", nullable = true)
    private UUID id;

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
