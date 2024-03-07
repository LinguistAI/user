package app.linguistai.bmvp.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.repository.IResetTokenRepository;
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

    public RLoginUser login(QUserLogin user) throws Exception {
        try {
            User dbUser = accountRepository.findUserByEmail(user.getEmail()).orElse(null);

            if (dbUser == null) {
                throw new Exception("User is not found");
            }

            String hashedPassword = dbUser.getPassword();
            boolean passwordMatch = bCryptPasswordEncoder.matches(user.getPassword(), hashedPassword);

            if (!passwordMatch) {
                throw new Exception("Passwords do not match");
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

            log.info(String.format("User %s logged in.", dbUser.getId()));

            return new RLoginUser(dbUser, accessToken, refreshToken);
        } catch (Exception e2) {
            System.out.println("login exception");
            throw e2;
        }
    }

    public RRefreshToken refreshToken(String auth) throws Exception {
        try {
            String username = jwtUtils.extractRefreshUsername(JWTUtils.getTokenWithoutBearer(auth));

            final UserDetails userDetails = jwtUserService.loadUserByUsername(username);
            final String accessToken = jwtUtils.createAccessToken(userDetails);
            return new RRefreshToken(accessToken);

        } catch (Exception e) {
            System.out.println("refresh token exception");
            throw e;
        }
    }

    public boolean changePassword(String email, QChangePassword passwords) throws Exception {
        try {
            User dbUser = accountRepository.findUserByEmail(email).orElse(null);

            if (dbUser == null) {
                throw new Exception("User is not found");
            }

            String hashedPassword = dbUser.getPassword();

            boolean passwordMatch = bCryptPasswordEncoder.matches(passwords.getOldPassword(), hashedPassword);

            if (!passwordMatch) {
                System.out.println("passwords does not match");
                throw new Exception("pasword no match");
            }

            // hash new password
            String hashedNewPassword = bCryptPasswordEncoder.encode(passwords.getNewPassword());

            dbUser.setPassword(hashedNewPassword);
            accountRepository.updatePassword(hashedNewPassword, dbUser.getId());

            log.info(String.format("User %s changed their password.", dbUser.getId()));

            return true;
        } catch (Exception e) {
            System.out.println("password change exception exception");
            throw e;
        }
    }

    @Transactional
    public User addUser(QUser requestUser) throws Exception {
        try {
            boolean userExist = accountRepository.existsByEmail(requestUser.getEmail());
            
            if (userExist) {
                throw new Exception("User already exists");
            }

            // generate uuid and hash password if user does not exist in the system
            requestUser.setId(UUID.randomUUID());
            requestUser.setPassword(encodePassword(requestUser.getPassword()));

            User newUser = accountRepository.save(new User(requestUser));

            // Create UserStreak for the new user
            if (!userStreakService.createUserStreak(newUser)) {
                throw new Exception("ERROR: Could not generate UserStreak for user with ID: [" + newUser.getId() + "]. Perhaps UserStreak already exists?");
            }

            log.info(String.format("User %s registered.", newUser.getId()));

            return newUser;            
        } catch (Exception e) {
            throw e;
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

    private String encodePassword(String plainPassword) { // TODO no need for this method
        try {
            return bCryptPasswordEncoder.encode(plainPassword);
        } catch (Exception e) {
            throw e;
        }
    }

    public ResetToken generateEmailToken(String email) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElse(null);

            if (user == null) {
                throw new NotFoundException("User with email [" + email + "] not found");
            }

            // invalidate previous reset tokens of user
            List<ResetToken> resetTokens = resetTokenRepository.findAllByUser(user);
            for (ResetToken resetToken: resetTokens) {
                resetToken.setUsed(true);
            }

            // create a new reset token
            ResetToken resetToken = new ResetToken(user);

            log.info(String.format("Email token is generated for user %s.", user.getId()));
            
            return resetTokenRepository.save(resetToken);
        } catch (Exception e) {
            System.out.println("Email token generation exception for email");
            throw e;
        }
    }

    public boolean validateResetCode(String email, String resetCode, boolean invalidate) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email).orElse(null);
            if (user == null) {
                throw new NotFoundException("User with email [" + email + "] not found");
            }

            ResetToken resetToken = resetTokenRepository.findByUserAndResetCode(user, resetCode).orElse(null);
            if (resetToken == null) {
                throw new NotFoundException("Reset token for user with email [" + user.getEmail() + "] with code [" + resetCode + "] not found.");
            }

            if (!isResetTokenValid(resetToken)) {
                return false;
            }

            if (invalidate) {
                resetToken.setUsed(true);
                resetTokenRepository.save(resetToken);
            }

            log.info(String.format("Reset code is validated for usre %s.", user.getId()));

            return true;
        } catch (Exception e) {
            System.out.println("Password reset token validation exception");
            throw e;
        }
    }

    private boolean isResetTokenValid(ResetToken resetToken) {
        return resetToken.getValidUntil() != null && !resetToken.getValidUntil().before(new Date()) && !resetToken.isUsed();
    }

    public boolean setPassword(String email, String password) throws Exception {
        User user = accountRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            throw new NotFoundException("User with email [" + email + "] not found");
        }

        String hashedPassword = encodePassword(password);
        user.setPassword(hashedPassword);

        int rowsAffected = accountRepository.updatePassword(hashedPassword, user.getId());

        log.info(String.format("New password is set for user %s.", user.getId()));

        return rowsAffected > 0;
    }
}
