package app.linguistai.bmvp.service.currency;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.currency.InsufficientGemsException;
import app.linguistai.bmvp.exception.currency.InvalidTransactionTypeException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.currency.Transaction;
import app.linguistai.bmvp.model.currency.UserGems;
import app.linguistai.bmvp.model.enums.TransactionType;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.currency.ITransactionRepository;
import app.linguistai.bmvp.repository.currency.IUserGemsRepository;
import jakarta.validation.constraints.NotBlank;
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
    private final IAccountRepository accountRepository;

    @Override
    @Transactional
    public UserGems processTransaction(@NotBlank String email, @NotNull TransactionType type, @NotNull Long amount) throws Exception {
        try {
            // Check if user exists, if so get the ID, if not throw a NotFoundException
            UUID userId = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true)).getId();

            // Check if user gems exists
            UserGems userGems = userGemsRepository.findById(userId).orElseThrow(() -> new NotFoundException(UserGems.class.getSimpleName(), true));

            switch (type) {
                case SHOP_SPEND -> {
                    if (!this.validateEnoughGems(userGems.getGems(), amount)) {
                        throw new InsufficientGemsException();
                    }

                    userGems.setGems(userGems.getGems() - amount);
                }
                case GEMS_PURCHASE, GEMS_REWARD -> userGems.setGems(userGems.getGems() + amount);
                default -> throw new InvalidTransactionTypeException();
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

            // logs userId instead of email as it is also the primary key of UserGems
            log.info("Processed transaction for user with ID {}: type: {} amount: {}", userId, type, amount);
            return saved;
        }
        catch (InvalidTransactionTypeException e) {
            log.error("Could not process transaction for user with email {}, unsupported transaction type", email);
            throw e;
        }
        catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("User is not found for email {}", email);
            }
            else if (e.getObject().equals(UserGems.class.getSimpleName())) {
                log.error("UserGems not found for user with email {}", email);
            }
            else { // Undefined NotFoundException
                log.error("Could not process transaction for user with email {}", email, e);
            }
            throw e;
        }
        catch (InsufficientGemsException e) {
            log.error("Not enough gems for user with email {}", email);
            throw e;
        }
        catch (Exception e) {
            log.error("Could not process transaction for user with email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    private Boolean validateEnoughGems(Long currentGems, Long itemCost) {
        return currentGems >= itemCost;
    }
}

