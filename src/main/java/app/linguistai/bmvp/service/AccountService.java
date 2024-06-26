package app.linguistai.bmvp.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import app.linguistai.bmvp.exception.AlreadyFoundException;
import app.linguistai.bmvp.exception.CustomException;
import app.linguistai.bmvp.exception.InvalidResetCodeException;
import app.linguistai.bmvp.exception.LoginException;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.PasswordNotMatchException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.StreakException;
import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.repository.IResetTokenRepository;
import app.linguistai.bmvp.service.currency.ITransactionService;
import app.linguistai.bmvp.service.gamification.IXPService;
import app.linguistai.bmvp.service.gamification.quest.IQuestService;
import app.linguistai.bmvp.service.profile.ProfileService;
import app.linguistai.bmvp.service.stats.UserLoggedDateService;
import app.linguistai.bmvp.service.wordbank.UnknownWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.UserSearchFriendshipStatus;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.request.QChangePassword;
import app.linguistai.bmvp.request.QUser;
import app.linguistai.bmvp.request.QUserLogin;
import app.linguistai.bmvp.request.QUserSearch;
import app.linguistai.bmvp.response.RLoginUser;
import app.linguistai.bmvp.response.RRefreshToken;
import app.linguistai.bmvp.response.RUserSearch;
import app.linguistai.bmvp.security.JWTUserService;
import app.linguistai.bmvp.security.JWTUtils;
import app.linguistai.bmvp.service.gamification.UserStreakService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static app.linguistai.bmvp.consts.FilePaths.DEFAULT_WORD_LIST_FILE;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    public static int hashStrength = 10;

    @Autowired
    final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final IAccountRepository accountRepository;
    private final IResetTokenRepository resetTokenRepository;
    private final JWTUserService jwtUserService;

    @Autowired
    private final JWTUtils jwtUtils;

    private final UserStreakService userStreakService;
    private final UserLoggedDateService userLoggedDateService;
    private final UnknownWordService unknownWordService;
    private final ITransactionService transactionService;
    private final IXPService xpService;
    private final IQuestService questService;
    private final ProfileService profileService;

    public RLoginUser login(QUserLogin user) throws Exception {
        try {
            User dbUser = accountRepository.findUserByEmail(user.getEmail()).orElseThrow(LoginException::new);

            String hashedPassword = dbUser.getPassword();
            boolean passwordMatch = bCryptPasswordEncoder.matches(user.getPassword(), hashedPassword);

            if (!passwordMatch) {
                throw new LoginException();
            }

            final UserDetails userDetails = jwtUserService.loadUserByUsername(user.getEmail());
            final String accessToken = jwtUtils.createAccessToken(userDetails);
            final String refreshToken = jwtUtils.createRefreshToken(userDetails);

            this.initiateUserSession(dbUser.getEmail());

            log.info("User {} logged in.", dbUser.getId());
            return new RLoginUser(dbUser, accessToken, refreshToken);
        } catch (CustomException e2) {
            log.error("User login failed due to wrong email or password for email {}", user.getEmail());
            throw e2;
        } catch (Exception e2) {
            log.error("User login failed for email {}", user.getEmail(), e2);
            throw new SomethingWentWrongException();
        }
    }

    public void loginWithValidToken(String email) throws Exception {
        // This method is only reached if the user already has a valid token
        try {
            this.initiateUserSession(email);
            log.info("User with email {} logged in with a valid token.", email);
        } catch (Exception e) {
            log.error("User login with valid token failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }
  
    public PageImpl<RUserSearch> searchUser(QUserSearch userSearch, String userEmail) throws Exception {
        try {
            System.out.println(userSearch.getPage());
            User dbUser = accountRepository.findUserByEmail(userEmail).orElseThrow(LoginException::new);

            // Create a page request using the request body
            PageRequest pageable = PageRequest.of(userSearch.getPage(), userSearch.getSize());

            Page<Object[]> users = accountRepository.findByUsernameStartingWithAndWithFriendshipStatusAndEmailNot(userSearch.getUsername(), dbUser.getId(), pageable);

            // Map query results into the response object
            List<RUserSearch> searchResults = users.getContent().stream()
                .map(entry -> {
                    User user = (User) entry[0];
                    UserSearchFriendshipStatus status = UserSearchFriendshipStatus.fromValue((int) entry[1]);

                    return new RUserSearch(user, status);
                })
                .collect(Collectors.toList());

            log.info("User {} searched for users {}.", userEmail, userSearch.getUsername());
            
            return new PageImpl<RUserSearch>(searchResults, pageable, users.getTotalElements());
        } catch (Exception e) {
            log.error("User {} search for users {} failed.", userEmail, userSearch.getUsername(), e);
            throw new SomethingWentWrongException();
        }
    }

    public RRefreshToken refreshToken(String auth) throws Exception {
        try {
            String username = jwtUtils.extractRefreshUsername(JWTUtils.getTokenWithoutBearer(auth));

            final UserDetails userDetails = jwtUserService.loadUserByUsername(username);
            final String accessToken = jwtUtils.createAccessToken(userDetails);

            return new RRefreshToken(accessToken);
        } catch (Exception e) {
            log.error("Error in generating new token with refresh token", e);
            throw new SomethingWentWrongException();
        }
    }

    public boolean changePassword(String email, QChangePassword passwords) throws Exception {
        try {
            User dbUser = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            String hashedPassword = dbUser.getPassword();

            boolean passwordMatch = bCryptPasswordEncoder.matches(passwords.getOldPassword(), hashedPassword);

            if (!passwordMatch) {
                throw new PasswordNotMatchException();
            }

            // hash new password
            String hashedNewPassword = bCryptPasswordEncoder.encode(passwords.getNewPassword());

            dbUser.setPassword(hashedNewPassword);
            accountRepository.updatePassword(hashedNewPassword, dbUser.getId());

            log.info("User {} changed their password.", dbUser.getId());

            return true;
        } catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        } catch (PasswordNotMatchException e) {
            log.error("User password did not match for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Change password failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RLoginUser addUser(QUser requestUser) throws Exception {
        try {
            // Check if email or username is already used before
            boolean userExist = accountRepository.existsByEmail(requestUser.getEmail());
            
            if (userExist) {
                throw new AlreadyFoundException("User already exists with the provided email address. Please use a different email or sign in.");
            }

            userExist = accountRepository.existsByUsername(requestUser.getUsername());

            if (userExist) {
                throw new AlreadyFoundException("User already exists with the provided username. Please use a different username or sign in.");
            }

            // Generate uuid and hash password if user does not exist in the system
            requestUser.setId(UUID.randomUUID());
            requestUser.setPassword(encodePassword(requestUser.getPassword()));

            User newUser = accountRepository.save(new User(requestUser));

            // Create UserStreak for the new user
            if (!userStreakService.createUserStreak(newUser)) {
                throw new StreakException();
            }

            xpService.createUserXPForRegister(newUser);
            profileService.createEmptyProfile(newUser);
            unknownWordService.addPredefinedWordList(DEFAULT_WORD_LIST_FILE, newUser.getEmail());
          
            // Create access and reset tokens so that user does not have to log in after registering
            final UserDetails userDetails = jwtUserService.loadUserByUsername(newUser.getEmail());
            final String accessToken = jwtUtils.createAccessToken(userDetails);
            final String refreshToken = jwtUtils.createRefreshToken(userDetails);

            this.initiateUserSession(newUser.getEmail());

            log.info("User registered with email {}.", newUser.getEmail());
            return new RLoginUser(newUser, accessToken, refreshToken);          
        } catch (AlreadyFoundException e) {
            log.error("User register fail since email already exists for email {}", requestUser.getEmail());
            throw e;
        } catch (StreakException e) {
            log.error("Could not generate UserStreak for user with email {}.", requestUser.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("User register failed for email {}", requestUser.getEmail(), e);
            throw new SomethingWentWrongException();
        }
    }

    public List<User> getUsers() {
        return accountRepository.findAll();
    }

    private String encodePassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    public ResetToken generateEmailToken(String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            // invalidate previous reset tokens of user
            List<ResetToken> resetTokens = resetTokenRepository.findAllByUser(user);
            for (ResetToken resetToken: resetTokens) {
                resetToken.setUsed(true);
            }

            // create a new reset token
            ResetToken resetToken = new ResetToken(user);

            log.info("Email token is generated for user with email {}.", email);
            
            return resetTokenRepository.save(resetToken);
        } catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Generate email token failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    public void validateResetCode(String email, String resetCode, boolean invalidate) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            ResetToken resetToken = resetTokenRepository.findByUserAndResetCode(user, resetCode).orElseThrow(() -> new NotFoundException("Reset token", true));

            if (!isResetTokenValid(resetToken)) throw new InvalidResetCodeException();

            if (invalidate) {
                resetToken.setUsed(true);
                resetTokenRepository.save(resetToken);
            }

            log.info("Reset code is validated for user {}", user.getId());
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("User is not found for email {}", email);
            } else {
                log.error("Reset token for user with email {} with code {} not found.", email, resetCode);
            }
            throw e;
        } catch (InvalidResetCodeException e) {
            log.error("Reset token for user with email {} with code {} is invalid.", email, resetCode);
            throw e;
        } catch (Exception e) {
            log.error("Validate reset code failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    private boolean isResetTokenValid(ResetToken resetToken) {
        return resetToken.getValidUntil() != null && !resetToken.getValidUntil().before(new Date()) && !resetToken.isUsed();
    }

    public boolean setPassword(String email, String password) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            String hashedPassword = encodePassword(password);
            user.setPassword(hashedPassword);

            int rowsAffected = accountRepository.updatePassword(hashedPassword, user.getId());

            log.info("New password is set for user with email {}.", email);

            return rowsAffected > 0;
        } catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Set password failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    private void initiateUserSession(String email) throws Exception {
        try {
            // Upon successful user entry, check whether to increase user streak or not
            userStreakService.updateUserStreak(email);

            // Add the current date as a logged date
            userLoggedDateService.addLoggedDateByEmailAndDate(email, new Date());

            questService.assignQuests(email);
            transactionService.ensureUserGemsExists(email);
        }
        catch (Exception e) {
            // Intentionally not thrown to not cause login exception for users without UserStreak
            log.error("User session initialization failed for email {}.", email, e);
        }

        log.info("User session initiated for email {}.", email);
    }
}
