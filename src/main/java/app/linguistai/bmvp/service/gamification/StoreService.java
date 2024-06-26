package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.exception.store.InsufficientUserItemQuantityException;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.currency.InsufficientGemsException;
import app.linguistai.bmvp.exception.store.ItemNotEnabledException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.TransactionType;
import app.linguistai.bmvp.model.currency.UserGems;
import app.linguistai.bmvp.model.gamification.store.StoreItem;
import app.linguistai.bmvp.model.gamification.store.UserItem;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.store.IStoreItemRepository;
import app.linguistai.bmvp.repository.gamification.store.IUserItemRepository;
import app.linguistai.bmvp.response.gamification.store.RQuizItems;
import app.linguistai.bmvp.response.gamification.store.RStoreItem;
import app.linguistai.bmvp.response.gamification.store.RStoreItems;
import app.linguistai.bmvp.response.gamification.store.RUserItem;
import app.linguistai.bmvp.response.gamification.store.RUserItems;
import app.linguistai.bmvp.service.currency.ITransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static app.linguistai.bmvp.consts.StoreConsts.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {
    private final IStoreItemRepository storeItemRepository;
    private final IUserItemRepository userItemRepository;
    private final IAccountRepository accountRepository;

    private final ITransactionService transactionService;

    @Transactional(readOnly = true)
    public RStoreItems getAllStoreItems(int page, int size) throws Exception {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StoreItem> storeItemsPage = storeItemRepository.findAll(pageable);
            List<RStoreItem> storeItems = storeItemsPage.getContent().stream().map(RStoreItem::new).collect(Collectors.toList());

            // Build a response and return all store items, enabled or disabled
            RStoreItems response = RStoreItems.builder().storeItems(storeItems)
                    .totalPages(storeItemsPage.getTotalPages())
                    .currentPage(storeItemsPage.getNumber())
                    .pageSize(storeItemsPage.getSize())
                    .build();
            log.info("All store items are fetched.");
            return response;
        } catch (Exception e) {
            log.error("Fetching store items failed", e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional(readOnly = true)
    public RStoreItems getAllEnabledStoreItems(int page, int size) throws SomethingWentWrongException {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StoreItem> enabledStoreItemsPage = storeItemRepository.findByEnabled(true, pageable);
            List<RStoreItem> enabledStoreItems = enabledStoreItemsPage.getContent().stream().map(RStoreItem::new).collect(Collectors.toList());

            // Build a response and return all enabled store items
            RStoreItems response = RStoreItems.builder().storeItems(enabledStoreItems)
                    .totalPages(enabledStoreItemsPage.getTotalPages())
                    .currentPage(enabledStoreItemsPage.getNumber())
                    .pageSize(enabledStoreItemsPage.getSize())
                    .build();
            log.info("All enabled store items are fetched.");
            return response;
        } catch (Exception e) {
            log.error("Fetching enabled store items failed", e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional(readOnly = true)
    public RQuizItems getAllQuizItems(String email, int page, int size) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            Pageable pageable = PageRequest.of(page, size);

            Page<StoreItem> storeItemsPage = storeItemRepository.findByTypeIn(QUIZ_TYPES, pageable);

            // Map store items to user items, creating items with 0 quantity if the user does not have the item
            List<RUserItem> userItems = storeItemsPage.getContent().stream()
                    .map(storeItem -> {
                        RUserItem rUserItem = getRUserItemFromStoreItem(user, storeItem);
                        if (rUserItem == null) {
                            log.info("Quiz type {} is not enabled in the store.", storeItem.getType());
                        }
                        return rUserItem;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Get the user's gems
            UserGems userGems = transactionService.ensureUserGemsExists(email);

            // Log success and return the result
            log.info("Items with quantities fetched successfully.");

            // Build the response
            return RQuizItems.builder()
                    .quizItems(userItems)
                    .totalPages(storeItemsPage.getTotalPages())
                    .currentPage(storeItemsPage.getNumber())
                    .pageSize(storeItemsPage.getSize())
                    .gems(userGems.getGems())
                    .build();
        } catch (NotFoundException e) {
            log.error("Fetching quiz items failed since user does not exist for email {}", email);
            throw e;
        } catch (Exception e) {
            // Log error and throw exception
            log.error("Fetching quiz items failed", e);
            throw new SomethingWentWrongException();
        }
    }


    @Transactional(readOnly = true)
    public RUserItems getUserItems(String email, int page, int size) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            Pageable pageable = PageRequest.of(page, size);
            Page<UserItem> userItemsPage = userItemRepository.findByUser(user, pageable);
            List<RUserItem> userItems = userItemsPage.getContent().stream()
                    .map(RUserItem::new)
                    .collect(Collectors.toList());

            // Build a response and return user's all items
            RUserItems response = RUserItems.builder().userItems(userItems)
                    .totalPages(userItemsPage.getTotalPages())
                    .currentPage(userItemsPage.getNumber())
                    .pageSize(userItemsPage.getSize())
                    .build();
            log.info("All user items are fetched for user with email {}", email);
            return response;
        } catch (NotFoundException e) {
            log.error("Fetching user items failed since user does not exist for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Fetching user items failed for user with email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RUserItem purchaseUserItem(String email, UUID storeItemId) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            StoreItem storeItem = storeItemRepository.findById(storeItemId)
                    .orElseThrow(() -> new NotFoundException(StoreItem.class.getSimpleName(), true));

            // Check if the store item is enabled
            if (!storeItem.isEnabled()) {
                throw new ItemNotEnabledException();
            }

            transactionService.processTransaction(email, TransactionType.SHOP_SPEND, storeItem.getPrice());

            // If the user already has the item, increment the quantity, otherwise create a new UserItem
            UserItem userItem = userItemRepository.findByUserAndStoreItem(user, storeItem)
                    .map(item -> {
                        // Check if incrementing the quantity will exceed Integer.MAX_VALUE
                        if (item.getQuantity() == Integer.MAX_VALUE) {
                            throw new IllegalStateException("Quantity exceeds integer bounds");
                        }
                        item.setQuantity(item.getQuantity() + 1);
                        return item;
                    })
                    .orElseGet(() -> new UserItem(user, storeItem, 1));

            // Save the user item
            userItemRepository.save(userItem);

            log.info("User {} purchased item {} successfully", email, storeItem.getType());
            return RUserItem.builder().userItem(userItem).build();
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("When purchasing an item, user is not found for email {}", email);
            } else {
                log.error("When purchasing an item, store item is not found for id {}", storeItemId);
            }
            throw e;
        } catch (ItemNotEnabledException e) {
            log.error("Store item with id {} is not enabled for purchase", storeItemId);
            throw e;
        } catch (InsufficientGemsException e) {
            throw e;
        } catch (IllegalStateException e) {
            log.error("Quantity exceeds integer bounds for UserItem with Store id {} and for user with email {}", storeItemId, email);
            throw e;
        } catch (Exception e) {
            log.error("Failed to purchase item", e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RUserItem decreaseUserItemQuantity(String email, String type) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            StoreItem storeItem = storeItemRepository.findByType(type)
                    .orElseThrow(() -> new NotFoundException(StoreItem.class.getSimpleName(), true));

            UserItem userItem = userItemRepository.findByUserAndStoreItem(user, storeItem)
                    .orElseThrow(() -> new NotFoundException(UserItem.class.getSimpleName(), true));

            consumeUserItem(userItem);

            log.info("User item consumed successfully for user with email {}", email);
            return RUserItem.builder().userItem(userItem).build();
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("When decreasing user item quantity, user is not found for email {}", email);
            } else if (e.getObject().equals(StoreItem.class.getSimpleName())) {
                log.error("When decreasing user item quantity, store item {} not found ", type);
            } else {
                log.error("When decreasing user item quantity, user item is not found for user email {} and store item {}",
                        email, type);
            }
            throw e;
        } catch (InsufficientUserItemQuantityException e) {
            log.error("User item quantity cannot be decreased since it is 0 or negative for user with email {} and store item {}", email, type);
            throw e;
        } catch (Exception e) {
            log.error("Failed to decrease user item quantity for user with email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    private void consumeUserItem(UserItem userItem) throws InsufficientUserItemQuantityException {
        if (userItem.getQuantity() <= 0) {
            throw new InsufficientUserItemQuantityException();
        }

        userItem.setQuantity(userItem.getQuantity() - 1);

        if (userItem.getQuantity() <= 0) {
            userItemRepository.delete(userItem);
        } else {
            userItemRepository.save(userItem);
        }
    }

    private RUserItem convertToRUserItem(UserItem userItem) {
        if (userItem != null) {
            return RUserItem.builder().userItem(userItem).build();
        } else {
            return null;
        }
    }

    private RUserItem getRUserItemFromStoreItem(User user, StoreItem storeItem) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByUserAndStoreItem(user, storeItem);
        // Return the user item if it exists
        if (optionalUserItem.isPresent()) {
            return convertToRUserItem(optionalUserItem.get());
        }
        // If the user does not have any of the item, create a new temporary user item with 0 quantity
        if (storeItem.isEnabled()) {
            return convertToRUserItem(new UserItem(user, storeItem, 0));
        }
        // Do not return disabled items that the user does not have
        return null;
    }
}
