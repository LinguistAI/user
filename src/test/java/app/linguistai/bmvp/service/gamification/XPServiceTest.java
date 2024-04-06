package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.configs.XPConfiguration;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.model.gamification.UserXP;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.IUserXPRepository;
import app.linguistai.bmvp.request.QUser;
import app.linguistai.bmvp.response.gamification.RUserXP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XPServiceTest {

    @Mock
    private XPConfiguration mockXp;
    @Mock
    private IAccountRepository mockAccountRepository;
    @Mock
    private IUserXPRepository mockXpRepository;

    @InjectMocks
    private XPService xpServiceUnderTest;

    @Test
    void testCreateUserXP() throws Exception {
        // Setup
        final RUserXP expectedResult = RUserXP.builder()
            .username("username")
            .currentExperience(0L)
            .totalExperienceToNextLevel(30L)
            .level(1L)
            .build();

        // Configure IAccountRepository.findUserByEmail(...).
        final QUser qUser = new QUser();
        qUser.setId(UUID.fromString("f893c036-878e-4262-8998-db4531ae3f09"));
        qUser.setUsername("username");
        qUser.setEmail("email");
        qUser.setPassword("password");
        final Optional<User> user = Optional.of(new User(qUser));
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(user);

        // Configure IUserXPRepository.save(...).
        final UserXP userXP = new UserXP();
        userXP.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user1 = new User();
        user1.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user1.setUsername("username");
        userXP.setUser(user1);
        userXP.setExperience(0L);
        final UserXP entity = new UserXP();
        entity.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user2 = new User();
        user2.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user2.setUsername("username");
        entity.setUser(user2);
        entity.setExperience(0L);
        when(mockXpRepository.save(any(UserXP.class))).thenReturn(userXP);
        when(mockXp.getBaseLevel()).thenReturn(30L);
        when(mockXp.getLevelCoefficient()).thenReturn(2L);

        // Run the test
        final RUserXP result = xpServiceUnderTest.createUserXP("email");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCreateUserXP_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> xpServiceUnderTest.createUserXP("email")).isInstanceOf(Exception.class);
    }

    @Test
    void testIncreaseUserXP_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> xpServiceUnderTest.increaseUserXP("email", XPAction.MESSAGE))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testIncreaseUserXP_IUserXPRepositoryFindByIdReturnsAbsent() throws Exception {
        // Setup
        final RUserXP expectedResult = RUserXP.builder()
            .username("username")
            .currentExperience(0L)
            .totalExperienceToNextLevel(30L)
            .level(1L)
            .build();

        // Configure IAccountRepository.findUserByEmail(...).
        final QUser qUser = new QUser();
        qUser.setId(UUID.fromString("f893c036-878e-4262-8998-db4531ae3f09"));
        qUser.setUsername("username");
        qUser.setEmail("email");
        qUser.setPassword("password");
        final Optional<User> user = Optional.of(new User(qUser));
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(user);

        when(mockXpRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        // Configure IUserXPRepository.save(...).
        final UserXP userXP = new UserXP();
        userXP.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user1 = new User();
        user1.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user1.setUsername("username");
        userXP.setUser(user1);
        userXP.setExperience(0L);
        final UserXP entity = new UserXP();
        entity.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user2 = new User();
        user2.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user2.setUsername("username");
        entity.setUser(user2);
        entity.setExperience(0L);
        when(mockXpRepository.save(any(UserXP.class))).thenReturn(userXP);
        when(mockXp.getBaseLevel()).thenReturn(30L);
        when(mockXp.getLevelCoefficient()).thenReturn(2L);

        // Run the test
        final RUserXP result = xpServiceUnderTest.increaseUserXP("email", XPAction.MESSAGE);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetUserXP() {
        // Setup
        // Configure IAccountRepository.findUserByEmail(...).
        final QUser qUser = new QUser();
        qUser.setId(UUID.fromString("f893c036-878e-4262-8998-db4531ae3f09"));
        qUser.setUsername("username");
        qUser.setEmail("email");
        qUser.setPassword("password");
        final Optional<User> user = Optional.of(new User(qUser));
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(user);

        // Configure IUserXPRepository.findById(...).
        final UserXP userXP1 = new UserXP();
        userXP1.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user1 = new User();
        user1.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user1.setUsername("username");
        userXP1.setUser(user1);
        userXP1.setExperience(0L);
        final Optional<UserXP> userXP = Optional.of(userXP1);
        when(mockXpRepository.findById(any(UUID.class))).thenReturn(userXP);

        // Run the test
        assertThrows(Exception.class, () -> xpServiceUnderTest.getUserXP("email"));
    }

    @Test
    void testGetUserXP_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> xpServiceUnderTest.getUserXP("email")).isInstanceOf(Exception.class);
    }

    @Test
    void testGetUserXP_IUserXPRepositoryFindByIdReturnsAbsent() throws Exception {
        // Setup
        final RUserXP expectedResult = RUserXP.builder()
            .username("username")
            .currentExperience(0L)
            .totalExperienceToNextLevel(30L)
            .level(1L)
            .build();

        // Configure IAccountRepository.findUserByEmail(...).
        final QUser qUser = new QUser();
        qUser.setId(UUID.fromString("f893c036-878e-4262-8998-db4531ae3f09"));
        qUser.setUsername("username");
        qUser.setEmail("email");
        qUser.setPassword("password");
        final Optional<User> user = Optional.of(new User(qUser));
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(user);

        when(mockXpRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        // Configure IUserXPRepository.save(...).
        final UserXP userXP = new UserXP();
        userXP.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user1 = new User();
        user1.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user1.setUsername("username");
        userXP.setUser(user1);
        userXP.setExperience(0L);
        final UserXP entity = new UserXP();
        entity.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user2 = new User();
        user2.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user2.setUsername("username");
        entity.setUser(user2);
        entity.setExperience(0L);
        when(mockXpRepository.save(any(UserXP.class))).thenReturn(userXP);
        when(mockXp.getBaseLevel()).thenReturn(30L);
        when(mockXp.getLevelCoefficient()).thenReturn(2L);

        // Run the test
        final RUserXP result = xpServiceUnderTest.getUserXP("email");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetUserOwnedXP() {
        // Setup
        final QUser qUser = new QUser();
        qUser.setId(UUID.fromString("f893c036-878e-4262-8998-db4531ae3f09"));
        qUser.setUsername("username");
        qUser.setEmail("email");
        qUser.setPassword("password");
        final UserXP userXP = new UserXP();
        userXP.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user = new User();
        user.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user.setUsername("username");
        userXP.setUser(user);
        userXP.setExperience(0L);

        // Configure IAccountRepository.findUserByEmail(...).
        final QUser qUser1 = new QUser();
        qUser1.setId(UUID.fromString("f893c036-878e-4262-8998-db4531ae3f09"));
        qUser1.setUsername("username");
        qUser1.setEmail("email");
        qUser1.setPassword("password");
        final Optional<User> user1 = Optional.of(new User(qUser1));
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(user1);

        // Configure IUserXPRepository.findById(...).
        final UserXP userXP2 = new UserXP();
        userXP2.setUserId(UUID.fromString("d1695edb-ca0f-4206-acec-0e435fa97eed"));
        final User user2 = new User();
        user2.setId(UUID.fromString("057ed9e8-4d08-486c-a681-df1e6da29b07"));
        user2.setUsername("username");
        userXP2.setUser(user2);
        userXP2.setExperience(0L);
        final Optional<UserXP> userXP1 = Optional.of(userXP2);
        when(mockXpRepository.findById(any(UUID.class))).thenReturn(userXP1);

        // Run the test
        assertThrows(Exception.class, () -> xpServiceUnderTest.getUserOwnedXP("email"));
    }

    @Test
    void testGetUserOwnedXP_IAccountRepositoryReturnsAbsent() {
        // Setup
        when(mockAccountRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> xpServiceUnderTest.getUserOwnedXP("email")).isInstanceOf(NotFoundException.class);
    }
}
