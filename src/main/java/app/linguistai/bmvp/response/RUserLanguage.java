package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class RUserLanguage {
    private UUID userId;
    private String username;
    private String email;
    private String language;
}
