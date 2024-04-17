package app.linguistai.bmvp.request.currency;

import app.linguistai.bmvp.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QTransactionRequest {
    @NotNull
    private TransactionType type;

    // Defining Min value as a constant in TransactionConsts gave "java: element value must be a constant expression" error
    @NotNull
    @Min(value = 1, message = "The transaction amount must be greater than zero")
    private Long amount;
}
