package app.linguistai.bmvp.service.gamification.quest;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.quest.Quest;
import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import app.linguistai.bmvp.model.gamification.quest.types.SendMessageCriteria;
import app.linguistai.bmvp.model.gamification.quest.types.UseWordCriteria;
import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.quest.IQuestRepository;
import app.linguistai.bmvp.request.gamification.QQuestSendMessage;
import app.linguistai.bmvp.request.gamification.QQuestTypeAction;
import app.linguistai.bmvp.service.wordbank.IUnknownWordService;
import app.linguistai.bmvp.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static app.linguistai.bmvp.consts.QuestConsts.*;


@RequiredArgsConstructor
@Service
public class QuestService implements IQuestService {
    private final IQuestRepository questRepository;

    private final IAccountRepository accountRepository;

    private final IUnknownWordService unknownWordService;



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
    @Transactional
    public void assignQuests(String email) throws Exception {
        try {
            // Step 1. Check if user exists
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            // Step 2. Check if user has active quests (assigned that day)
            if (this.userHasActiveQuests(user)) {
                return; // Step 2.1 If user has active quests for today, return without modification
            }

            // Step 2.2 If the user does not have any quests/quests are older than a day, remove the old quests
            this.deleteInactiveQuests(user);

            // Step 3. Assign 2 "Word Practice" quests for different words
            List<Quest> useWordQuests = this.buildBulkUseWordQuests(user, ASSIGN_USE_WORD_QUEST_AMOUNT);

            // Step 4. Assign a "Send Message" quest
            Integer randomSendMessagedTimes = this.generateRandomTimesForUseWordQuest();
            Quest sendMessage = this.buildSendMessageQuest(user, randomSendMessagedTimes);

            // Step 5. Save all quests to the repository
            List<Quest> questsToAssign = new ArrayList<>(useWordQuests);
            questsToAssign.add(sendMessage);

            questRepository.saveAll(questsToAssign);
        }
        catch (Exception e) {
            System.out.println("ERROR: Could not assign quests to user with email " + email + ".");
            throw e;
        }
    }

    private List<Quest> buildBulkUseWordQuests(User user, Integer count) throws Exception {
        if (count <= 0) {
            throw new IllegalArgumentException("ERROR: Build bulk use word quests method failed. Count must be greater than zero.");
        }

        List<Quest> useWordQuests = new ArrayList<>();
        HashSet<String> usedWords = new HashSet<>();

        for (int i = 0; i < count; i++) {
            // Get one active unknown word list
            UnknownWordList randomActiveList = unknownWordService.getRandomActiveUnknownWordList(user.getId());

            // Select a random word from the retrieved unknown word list, while ensuring no duplicates exist for quests
            String randomWord = this.returnUniqueRandomWordForUseWordQuest(randomActiveList.getListId(), usedWords);
            usedWords.add(randomWord);

            // Generate a random "times" field
            Integer randomUseWordTimes = this.generateRandomTimesForUseWordQuest();

            // Build the Use Word quest and add to the results list
            useWordQuests.add(this.buildUseWordQuest(user, randomWord, randomUseWordTimes));
        }

        return useWordQuests;
    }

    /**
     * Select a random word from the retrieved unknown word list, while ensuring no duplicates exist for quests
     * @param listId List ID for which the words will be pulled from
     * @param usedWordsSoFar Used words so far in the generation
     * @return Random unique word for "Use Word" type quests
     * @throws Exception When a random word cannot be retrieved from list with ID listId
     */
    private String returnUniqueRandomWordForUseWordQuest(UUID listId, HashSet<String> usedWordsSoFar) throws Exception {
        final int HARDCODED_DO_WHILE_LIMIT = 100; // to prevent infinite loops
        int currentIteration = 0;
        String randomWord;

        do {
            randomWord = unknownWordService.getRandomWordFromList(listId);
            currentIteration++;
        } while (usedWordsSoFar.contains(randomWord) && currentIteration < HARDCODED_DO_WHILE_LIMIT);

        return randomWord;
    }

    private Quest buildUseWordQuest(User assignee, String word, Integer times) {
        return Quest.builder()
            .user(assignee)
            .title(WORD_PRACTICE_TITLE)
            .description(this.generateUseWordQuestDesc(word, times))
            .type(TYPE_USE_WORD)
            .image(QUEST_WORD_IMAGE)
            .reward(BASE_USE_WORD_REWARD * times)
            .assignedDate(DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime()))
            .completionCriteria(new UseWordCriteria(word, times))
            .build();
    }

    private Quest buildSendMessageQuest(User assignee, Integer times) {
        return Quest.builder()
            .user(assignee)
            .title(SEND_MESSAGE_TITLE)
            .description(this.generateSendMessageQuestDesc(times))
            .type(TYPE_SEND_MESSAGE)
            .image(QUEST_MESSAGE_IMAGE)
            .reward(BASE_SEND_MESSAGE_REWARD * times)
            .assignedDate(DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime()))
            .completionCriteria(new SendMessageCriteria(times))
            .build();
    }

    private Boolean userHasActiveQuests(User user) {
        List<Quest> userQuests = questRepository.findAllByUserId(user.getId());
        java.sql.Date today = DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime());
        return userQuests.stream().anyMatch(quest -> DateUtils.isSqlDatesEqual(quest.getAssignedDate(), today));
    }

    private void deleteInactiveQuests(User user) {
        // Get all user quests
        List<Quest> userQuestsToRemove = questRepository.findAllByUserId(user.getId());

        // Get today's date in java.sql.Date format
        java.sql.Date today = DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime());

        // Remove all quests assigned today (so they are not deleted after questRepository.deleteAll(userQuestsToRemove)
        userQuestsToRemove.removeIf(quest -> quest.getAssignedDate().equals(today));

        // Delete all inactive quests from the repository
        questRepository.deleteAll(userQuestsToRemove);
    }

    private String generateUseWordQuestDesc(String word, Integer times) {
        return this.replaceTimes(WORD_PRACTICE_DESC.replace(WORD_IDENTIFIER, word), times);
    }

    private String generateSendMessageQuestDesc(Integer times) {
        return this.replaceTimes(SEND_MESSAGE_DESC, times);
    }

    private String replaceTimes(String template, Integer times) {
        return template.replace(TIMES_IDENTIFIER, times.toString());
    }

    private Integer generateRandomTimesForUseWordQuest() {
        return this.generateRandomTimes(USE_WORD_TIMES_UPPER_LIMIT, USE_WORD_TIMES_LOWER_LIMIT);
    }

    private Integer generateRandomTimesForSendMessageQuest() {
        return this.generateRandomTimes(SEND_MESSAGE_TIMES_UPPER_LIMIT, SEND_MESSAGE_TIMES_LOWER_LIMIT);
    }

    private Integer generateRandomTimes(Integer upperLimit, Integer lowerLimit) {
        // Random().nextInt() returns    [0         , upperLimit - lowerLimit + 1).
        // Hence, the below line returns [lowerLimit, upperLimit + 1),
        // which is the same as          [lowerLimit, upperLimit].
        return new Random().nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
    }
}
