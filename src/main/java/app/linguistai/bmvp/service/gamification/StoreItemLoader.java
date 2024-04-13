package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.model.gamification.store.StoreItem;
import app.linguistai.bmvp.repository.gamification.store.IStoreItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import static app.linguistai.bmvp.consts.StoreConsts.*;

@Slf4j
@Component
public class StoreItemLoader implements ApplicationRunner {

    private final IStoreItemRepository storeItemRepository;

    @Autowired
    public StoreItemLoader(IStoreItemRepository storeItemRepository) {
        this.storeItemRepository = storeItemRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StoreItem[] storeItems = {
                new StoreItem(TYPE_DOUBLE_ANSWER, DESCRIPTION_DOUBLE_ANSWER, PRICE_DOUBLE_ANSWER, ENABLED_DOUBLE_ANSWER),
                new StoreItem(TYPE_ELIMINATE_WRONG_ANSWER, DESCRIPTION_ELIMINATE_WRONG_ANSWER, PRICE_ELIMINATE_WRONG_ANSWER, ENABLED_ELIMINATE_WRONG_ANSWER),
        };

        try {
            // Save store items by updating the existing ones and creating new items for new "types"
            for (StoreItem newItem : storeItems) {
                StoreItem existingItem = storeItemRepository.findByType(newItem.getType()).orElse(null);
                if (existingItem != null) {
                    existingItem.setDescription(newItem.getDescription());
                    existingItem.setGemPrice(newItem.getGemPrice());
                    existingItem.setEnabled(newItem.isEnabled());
                    storeItemRepository.save(existingItem);
                } else {
                    storeItemRepository.save(newItem);
                }
            }
            log.info("Store items loaded into the database successfully.");
        } catch (DataIntegrityViolationException e) {
            log.info("Store items already exist in the database.");
        } catch (Exception e) {
            log.error("Failed to load store items.", e);
            e.printStackTrace();
        }
    }
}