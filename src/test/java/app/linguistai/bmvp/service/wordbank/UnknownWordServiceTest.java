package app.linguistai.bmvp.service.wordbank;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.ConfidenceEnum;
import app.linguistai.bmvp.model.wordbank.ListStats;
import app.linguistai.bmvp.model.wordbank.UnknownWord;
import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordListRepository;
import app.linguistai.bmvp.repository.wordbank.IUnknownWordRepository;
import app.linguistai.bmvp.request.wordbank.QAddUnknownWord;
import app.linguistai.bmvp.request.wordbank.QUnknownWordList;
import app.linguistai.bmvp.response.wordbank.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnknownWordServiceTest {

    @InjectMocks
    UnknownWordService unknownWordService;

    @Mock
    private IUnknownWordRepository wordRepository;
    @Mock
    private IUnknownWordListRepository listRepository;
    @Mock
    private IAccountRepository accountRepository;

    @Test
    void testGetListsByEmail() throws Exception {
        // Setup
        final RUnknownWordLists expectedResult = RUnknownWordLists.builder()
            .ownerUsername("ownerUsername")
            .lists(List.of(RUnknownWordList.builder()
                .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
                .title("title")
                .description("description")
                .isActive(false)
                .isFavorite(false)
                .isPinned(false)
                .imageUrl("imageUrl")
                .listStats(new ListStats(0L, 0L, 1L))
                .build()))
            .build();

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        // Configure IUnknownWordListRepository.findByUserId(...).
        final List<UnknownWordList> unknownWordLists = List.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findByUserId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d")))
            .thenReturn(unknownWordLists);

        // Configure IUnknownWordListRepository.findById(...).
        final Optional<UnknownWordList> optionalUnknownWordList = Optional.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(optionalUnknownWordList);

        // Configure IUnknownWordRepository.findByOwnerListListId(...).
        final List<UnknownWord> unknownWords = List.of(UnknownWord.builder()
            .ownerList(UnknownWordList.builder()
                .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
                .user(new User())
                .title("title")
                .description("description")
                .isActive(false)
                .isFavorite(false)
                .isPinned(false)
                .imageUrl("imageUrl")
                .build())
            .word("word")
            .confidence(ConfidenceEnum.LOWEST)
            .build());
        when(wordRepository.findByOwnerListListId(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))).thenReturn(unknownWords);

        // Run the test
        final RUnknownWordLists result = unknownWordService.getListsByEmail("email");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetListsByEmail_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.getListsByEmail("email")).isInstanceOf(Exception.class);
    }

    @Test
    void testGetListsByEmail_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        // Configure IUnknownWordListRepository.findByUserId(...).
        final List<UnknownWordList> unknownWordLists = List.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findByUserId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d")))
            .thenReturn(unknownWordLists);

        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.getListsByEmail("email")).isInstanceOf(Exception.class);
    }

    @Test
    void testGetListsByEmail_IUnknownWordRepositoryReturnsNoItems() throws Exception {
        // Setup
        final RUnknownWordLists expectedResult = RUnknownWordLists.builder()
            .ownerUsername("ownerUsername")
            .lists(List.of(RUnknownWordList.builder()
                .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
                .title("title")
                .description("description")
                .isActive(false)
                .isFavorite(false)
                .isPinned(false)
                .imageUrl("imageUrl")
                .listStats(new ListStats(0L, 0L, 0L))
                .build()))
            .build();

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        // Configure IUnknownWordListRepository.findByUserId(...).
        final List<UnknownWordList> unknownWordLists = List.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findByUserId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d")))
            .thenReturn(unknownWordLists);

        // Configure IUnknownWordListRepository.findById(...).
        final Optional<UnknownWordList> optionalUnknownWordList = Optional.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(optionalUnknownWordList);

        when(wordRepository.findByOwnerListListId(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))).thenReturn(Collections.emptyList());

        // Run the test
        final RUnknownWordLists result = unknownWordService.getListsByEmail("email");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetListWithWordsById_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.getListWithWordsById(
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"), "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testGetListWithWordsById_IUnknownWordListRepositoryReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.getListWithWordsById(
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"), "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testCreateList_IAccountRepositoryReturnsAbsent() {
        // Setup
        final QUnknownWordList qUnknownWordList = new QUnknownWordList("title", "description", false, false, false,
            "imageUrl");
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.createList(qUnknownWordList, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testCreateList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        final QUnknownWordList qUnknownWordList = new QUnknownWordList("title", "description", false, false, false,
            "imageUrl");

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        // Configure IUnknownWordListRepository.save(...).
        final UnknownWordList unknownWordList = UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build();
        when(listRepository.save(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build())).thenReturn(unknownWordList);

        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.createList(qUnknownWordList, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testEditList_IAccountRepositoryReturnsAbsent() {
        // Setup
        final QUnknownWordList qUnknownWordList = new QUnknownWordList("title", "description", false, false, false,
            "imageUrl");
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.editList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                qUnknownWordList, "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testEditList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        final QUnknownWordList qUnknownWordList = new QUnknownWordList("title", "description", false, false, false,
            "imageUrl");

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.editList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                qUnknownWordList, "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testAddWord_IAccountRepositoryReturnsAbsent() {
        // Setup
        final QAddUnknownWord qAddUnknownWord = new QAddUnknownWord("word",
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"));
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.addWord(qAddUnknownWord, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testAddWord_IUnknownWordListRepositoryReturnsAbsent() {
        // Setup
        final QAddUnknownWord qAddUnknownWord = new QAddUnknownWord("word",
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"));

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.addWord(qAddUnknownWord, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testIncreaseConfidence1_IAccountRepositoryReturnsAbsent() {
        // Setup
        final QAddUnknownWord qAddUnknownWord = new QAddUnknownWord("word",
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"));
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.increaseConfidence(qAddUnknownWord, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testIncreaseConfidence1_IUnknownWordListRepositoryReturnsAbsent() {
        // Setup
        final QAddUnknownWord qAddUnknownWord = new QAddUnknownWord("word",
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"));

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.increaseConfidence(qAddUnknownWord, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testDecreaseConfidence1_IAccountRepositoryReturnsAbsent() {
        // Setup
        final QAddUnknownWord qAddUnknownWord = new QAddUnknownWord("word",
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"));
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.decreaseConfidence(qAddUnknownWord, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testDecreaseConfidence1_IUnknownWordListRepositoryReturnsAbsent() {
        // Setup
        final QAddUnknownWord qAddUnknownWord = new QAddUnknownWord("word",
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"));

        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.decreaseConfidence(qAddUnknownWord, "email"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testActivateList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.activateList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testActivateList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.activateList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testDeactivateList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.deactivateList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testDeactivateList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.deactivateList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testAddFavoriteList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.addFavoriteList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testAddFavoriteList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.addFavoriteList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testRemoveFavoriteList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.removeFavoriteList(
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"), "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testRemoveFavoriteList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.removeFavoriteList(
            UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"), "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testPinList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.pinList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testPinList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.pinList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testUnpinList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.unpinList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testUnpinList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.unpinList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testDeleteList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.deleteList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testDeleteList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.deleteList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testModifyWord_IUnknownWordListRepositoryReturnsAbsent() {
        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.modifyWord(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email", "word", 0)).isInstanceOf(Exception.class);
    }

    @Test
    void testModifyWord_IUnknownWordRepositoryFindByOwnerListListIdAndWordReturnsAbsent() {
        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.modifyWord(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email", "word", 0)).isInstanceOf(Exception.class);
    }

    @Test
    void testModifyList_IAccountRepositoryReturnsAbsent() {
        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.modifyList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email", false, 0)).isInstanceOf(Exception.class);
    }

    @Test
    void testModifyList_IUnknownWordListRepositoryFindByIdReturnsAbsent() {
        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.modifyList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email", false, 0)).isInstanceOf(Exception.class);
    }

    @Test
    void testGetUserOwnedList_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(accountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.getUserOwnedList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(Exception.class);
    }

    @Test
    void testGetUserOwnedList_IUnknownWordListRepositoryReturnsAbsent() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final User user1 = new User();
        user1.setId(UUID.fromString("67a444dc-14f4-4abc-bd83-b1c380f2004d"));
        user1.setUsername("ownerUsername");
        user1.setEmail("email");
        user1.setPassword("password");
        final Optional<User> user = Optional.of(user1);
        when(accountRepository.findUserByEmail("email")).thenReturn(user);

        when(listRepository.findById(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
            () -> unknownWordService.getUserOwnedList(UUID.fromString("7fc2c3d9-f3b1-4855-abdd-2c8894b78cc5"),
                "email")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetListStats() throws Exception {
        // Setup
        final ListStats expectedResult = new ListStats(0L, 0L, 1L);

        // Configure IUnknownWordListRepository.findById(...).
        final Optional<UnknownWordList> optionalUnknownWordList = Optional.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(optionalUnknownWordList);

        // Configure IUnknownWordRepository.findByOwnerListListId(...).
        final List<UnknownWord> unknownWords = List.of(UnknownWord.builder()
            .ownerList(UnknownWordList.builder()
                .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
                .user(new User())
                .title("title")
                .description("description")
                .isActive(false)
                .isFavorite(false)
                .isPinned(false)
                .imageUrl("imageUrl")
                .build())
            .word("word")
            .confidence(ConfidenceEnum.LOWEST)
            .build());
        when(wordRepository.findByOwnerListListId(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))).thenReturn(unknownWords);

        // Run the test
        final ListStats result = unknownWordService.getListStats(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetListStats_IUnknownWordListRepositoryReturnsAbsent() {
        // Setup
        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> unknownWordService.getListStats(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetListStats_IUnknownWordRepositoryReturnsNoItems() throws Exception {
        // Setup
        final ListStats expectedResult = new ListStats(0L, 0L, 0L);

        // Configure IUnknownWordListRepository.findById(...).
        final Optional<UnknownWordList> optionalUnknownWordList = Optional.of(UnknownWordList.builder()
            .listId(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))
            .user(new User())
            .title("title")
            .description("description")
            .isActive(false)
            .isFavorite(false)
            .isPinned(false)
            .imageUrl("imageUrl")
            .build());
        when(listRepository.findById(UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382")))
            .thenReturn(optionalUnknownWordList);

        when(wordRepository.findByOwnerListListId(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"))).thenReturn(Collections.emptyList());

        // Run the test
        final ListStats result = unknownWordService.getListStats(
            UUID.fromString("cc04e013-6d5e-4015-887f-5e5a9bc58382"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
