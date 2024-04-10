package app.linguistai.bmvp.model.currency;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static app.linguistai.bmvp.consts.TransactionConsts.MIN_TRANSACTION_AMOUNT;

@Data
@Builder
@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @Column(name = "transaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "type", nullable = false)
    private TransactionType transactionType;

    @NotNull
    @Min(value = MIN_TRANSACTION_AMOUNT, message = "The transaction amount must be greater than zero.")
    @Column(name = "amount", nullable = false)
    private Long amount;
}
