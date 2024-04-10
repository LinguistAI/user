package app.linguistai.bmvp.service.currency;

import app.linguistai.bmvp.model.currency.UserGems;
import app.linguistai.bmvp.model.enums.TransactionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ITransactionService {
    UserGems processTransaction(@NotBlank @Email String email, @NotNull TransactionType type, @NotNull Long amount) throws Exception;
    UserGems ensureUserGemsExists(@NotBlank @Email String email) throws Exception;
}
