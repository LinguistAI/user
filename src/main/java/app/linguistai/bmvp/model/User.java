package app.linguistai.bmvp.model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import app.linguistai.bmvp.request.QUser;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import static app.linguistai.bmvp.consts.LanguageCodes.CODE_ENGLISH;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email field must be in a valid email format")
    private String email;

    @JsonIgnore
    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "language", nullable = false)
    private String currentLanguage;

    @Nullable
    @Column(name = "marked_for_deletion", nullable = true)
    private Boolean markedForDeletion = false;

    @Nullable
    @Column(name = "marked_for_deletion_date")
    private Date markedForDeletionDate;

    public User(QUser reqUser) {
        this.id = reqUser.getId();
        this.username = reqUser.getUsername();
        this.email = reqUser.getEmail();
        this.password = reqUser.getPassword();
        this.currentLanguage = CODE_ENGLISH;
    }
}
