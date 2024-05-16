package app.linguistai.bmvp.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QUser {
    private UUID id;
    private String username;

    @Email(message = "Email field must be in a valid email format")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).*$", 
    message = "Password must contain at least one uppercase letter, one number, and one special character")
    private String password;

    
    private String fcmToken;
}