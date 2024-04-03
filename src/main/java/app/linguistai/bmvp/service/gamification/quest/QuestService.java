package app.linguistai.bmvp.service.gamification.quest;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.quest.Quest;
import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.quest.IQuestRepository;
import app.linguistai.bmvp.request.gamification.QQuestSendMessage;
import app.linguistai.bmvp.request.gamification.QQuestTypeAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class QuestService implements IQuestService {
    private final IQuestRepository questRepository;

    private final IAccountRepository accountRepository;

    @Override
    public void processSendMessage(String email, QQuestSendMessage message) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        }
        catch (Exception e) {
            System.out.println("ERROR: Could not process quest send message quest action.");
            throw e;
        }
    }

    @Override
    public void processQuestTypeAction(String email, QuestCompletionCriteria type, QQuestTypeAction action) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        }
        catch (Exception e) {
            System.out.println("ERROR: Could not process quest type action.");
            throw e;
        }
    }

    @Override
    public Boolean checkUserHasQuestType(String email, QuestCompletionCriteria type) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            return false;
        }
        catch (Exception e) {
            System.out.println("ERROR: Could not check if the user with email " + email + " has quest type " + type.getClass().getSimpleName() + ".");
            throw e;
        }
    }

    @Override
    public void assignQuests(String email) {
        try {
            // Step 1. Check if user exists

            // Step 2. Check if user has active quests (assigned that day)

            // Step 2.1 If user has active quests, return without modification

            // Step 2.2 If the user does not have any quests/quests are older than a day, assign new quests

            // Step 3. Get one active unknown word list

            // Step 4. Select a random word from the retrieved unknown word list

            // Step 5. Assign a "Word Practice" quest for the selected word

            // Step 6. Randomly assign the remaining 2 quests
        }
        catch (Exception e) {
            System.out.println("ERROR: Could not assign quests to user with email " + email + ".");
            throw e;
        }
    }

    private Quest createWordPracticeQuest(String word, Integer times) {
        return null;
    }
}
