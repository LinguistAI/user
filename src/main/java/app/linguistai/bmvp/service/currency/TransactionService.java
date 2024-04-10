package app.linguistai.bmvp.service.currency;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.model.currency.Transaction;
import app.linguistai.bmvp.model.currency.UserGems;
import app.linguistai.bmvp.model.enums.TransactionType;
import app.linguistai.bmvp.repository.currency.ITransactionRepository;
import app.linguistai.bmvp.repository.currency.IUserGemsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class TransactionService implements ITransactionService {
    private final ITransactionRepository transactionRepository;
    private final IUserGemsRepository userGemsRepository;

    @Override
    @Transactional
    public UserGems processTransaction(@NotNull UUID userId, @NotNull TransactionType type, @NotNull Long amount) throws Exception {
        try {
            UserGems userGems = userGemsRepository.findById(userId).orElseThrow(() -> new NotFoundException(UserGems.class.getSimpleName(), true));

            switch (type) {
                case SHOP_SPEND -> {
                    if (!this.validateEnoughGems(userGems.getGems(), amount)) {
                        throw new RuntimeException("Not enough gems.");
                    }

                    userGems.setGems(userGems.getGems() - amount);
                }
                case GEMS_PURCHASE, GEMS_REWARD -> userGems.setGems(userGems.getGems() + amount);
                default -> throw new IllegalArgumentException("Unsupported transaction type.");
            }

            // Save the new gem count of user to the repository
            UserGems saved = userGemsRepository.save(userGems);

            // Save the transaction
            transactionRepository.save(Transaction.builder()
                .user(userGems.getUser())
                .transactionType(type)
                .amount(amount)
                .build()
            );

            log.info("Processed transaction for user with ID {}: type: {} amount: {}", userId, type, amount);
            return saved;
        }
        catch (IllegalArgumentException e) {
            log.error("Could not process transaction for user with ID {}, unsupported transaction type", userId);
            throw e;
        }
        catch (NotFoundException e) {
            log.error("UserGems not found for user with ID {}", userId);
            throw e;
        }
        catch (RuntimeException e) {
            log.error("Not enough gems for user with ID {}", userId);
            throw e;
        }
        catch (Exception e) {
            log.error("Could not process transaction for user with ID {}", userId, e);
            throw new SomethingWentWrongException();
        }
    }

    private Boolean validateEnoughGems(Long currentGems, Long itemCost) {
        return currentGems >= itemCost;
    }
}

