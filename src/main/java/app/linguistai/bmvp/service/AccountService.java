package app.linguistai.bmvp.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.exception.AlreadyFoundException;
import app.linguistai.bmvp.exception.CustomException;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.exception.LoginException;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.PasswordNotMatchException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.StreakException;
import app.linguistai.bmvp.exception.TokenException;
import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.repository.IResetTokenRepository;
import app.linguistai.bmvp.service.stats.UserLoggedDateService;
import app.linguistai.bmvp.service.wordbank.UnknownWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.request.QChangePassword;
import app.linguistai.bmvp.request.QUser;
import app.linguistai.bmvp.request.QUserLogin;
import app.linguistai.bmvp.response.RLoginUser;
import app.linguistai.bmvp.response.RRefreshToken;
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

    public RLoginUser login(QUserLogin user) throws Exception {
        try {
            User dbUser = accountRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new LoginException());

            String hashedPassword = dbUser.getPassword();
            boolean passwordMatch = bCryptPasswordEncoder.matches(user.getPassword(), hashedPassword);

            if (!passwordMatch) {
                throw new LoginException();
            }

            final UserDetails userDetails = jwtUserService.loadUserByUsername(user.getEmail());
            final String accessToken = jwtUtils.createAccessToken(userDetails);
            final String refreshToken = jwtUtils.createRefreshToken(userDetails);

            // If login is successful, check whether to increase user streak or not
            // ALSO IN MESSAGE SERVICE: userStreakService.updateUserStreak(dbUser.getEmail());
            try {
                userStreakService.updateUserStreak(dbUser.getEmail());
            }
            catch (Exception e1) {
                // Intentionally not thrown to not cause login exception for users without UserStreak
                System.out.println(ExceptionLogger.log(e1));
            }

            log.info("User {} logged in.", dbUser.getId());
            // Add the current date as a logged date
            userLoggedDateService.addLoggedDateByEmailAndDate(dbUser.getEmail(), new Date());

            return new RLoginUser(dbUser, accessToken, refreshToken);
        } catch (CustomException e2) {
            log.error("User login failed due to wrong email or password for email {}", user.getEmail());

            throw e2;
        } catch (Exception e2) {
            log.error("User login failed for email {}", user.getEmail(), e2);
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
            // check if email or username is already used before
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
          
            unknownWordService.addPredefinedWordList(DEFAULT_WORD_LIST_FILE, newUser.getEmail());
          
            // Create access and reset tokens so that user does not have to log in after registering
            final UserDetails userDetails = jwtUserService.loadUserByUsername(newUser.getEmail());
            final String accessToken = jwtUtils.createAccessToken(userDetails);
            final String refreshToken = jwtUtils.createRefreshToken(userDetails);

            log.info("User registered with email {}.", newUser.getId());

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
        try {
            // return accountRepository.findAll().stream().map(user -> new RUser(user)).collect(Collectors.toList());
            return accountRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }

    private String encodePassword(String plainPassword) {
        try {
            return bCryptPasswordEncoder.encode(plainPassword);
        } catch (Exception e) {
            throw e;
        }
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

    public boolean validateResetCode(String email, String resetCode, boolean invalidate) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            ResetToken resetToken = resetTokenRepository.findByUserAndResetCode(user, resetCode).orElseThrow(() -> new NotFoundException("Reset token", true));

            if (!isResetTokenValid(resetToken)) {
                return false;
            }

            if (invalidate) {
                resetToken.setUsed(true);
                resetTokenRepository.save(resetToken);
            }

            log.info("Reset code is validated for user {}", user.getId());

            return true;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("User is not found for email {}", email);
            } else {
                log.error("Reset token for user with email {} with code {} not found.", email, resetCode);
            }

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

            log.info("New password is set for user %s.", user.getId());

            return rowsAffected > 0;
        } catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Set password failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }
}
