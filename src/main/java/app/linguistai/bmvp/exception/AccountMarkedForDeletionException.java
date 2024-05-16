package app.linguistai.bmvp.exception;

import org.springframework.http.HttpStatus;

public class AccountMarkedForDeletionException extends CustomException {
    public AccountMarkedForDeletionException() {
        super("This account is marked for deletion", HttpStatus.FORBIDDEN);
    }
}
