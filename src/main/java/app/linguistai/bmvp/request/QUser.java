package app.linguistai.bmvp.request;

import java.util.UUID;

import lombok.Data;

@Data
public class QUser {
    private UUID id;
    private String username;
    private String email;
    private String password;
}