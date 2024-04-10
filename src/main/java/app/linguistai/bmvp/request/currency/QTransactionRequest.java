package app.linguistai.bmvp.request.currency;

import app.linguistai.bmvp.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static app.linguistai.bmvp.consts.TransactionConsts.MIN_TRANSACTION_AMOUNT;

@Data
public class QTransactionRequest {
    @NotNull
    private TransactionType type;

    @NotNull
    @Min(value = MIN_TRANSACTION_AMOUNT, message = "The transaction amount must be greater than zero")
    private Long amount;
}
