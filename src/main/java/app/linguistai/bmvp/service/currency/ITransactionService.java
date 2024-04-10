package app.linguistai.bmvp.service.currency;

import app.linguistai.bmvp.model.currency.UserGems;
import app.linguistai.bmvp.model.enums.TransactionType;
import java.util.UUID;

public interface ITransactionService {
    UserGems processTransaction(UUID userId, TransactionType type, Long amount) throws Exception;
}
