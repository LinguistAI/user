package app.linguistai.bmvp.service.gamification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.response.RUserStreak;
import org.springframework.stereotype.Service;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.UserStreak;
import app.linguistai.bmvp.repository.gamification.IUserStreakRepository;
import app.linguistai.bmvp.utils.DateUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserStreakService {
    private final IUserStreakRepository userStreakRepository;

    private final IAccountRepository accountRepository;

    public RUserStreak updateUserStreak(String email) throws Exception {
        try {
            return checkUserStreakForUpdate(getUserStreakWithEmail(email));
        }
        catch (Exception e) {
            System.out.println("ERROR: Could not check user streak for update with email: [" + email + "].");
            throw e;
        }
    }

    private RUserStreak checkUserStreakForUpdate(RUserStreak rUserStreak) throws Exception {
        try {
            Date streakTime = DateUtils.convertSqlDateToUtilDate(rUserStreak.getLastLogin());

            if (streakTime == null) {
                throw new Exception("StreakTime is null");
            }

            LocalDate lastLoginLocalDate = streakTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = LocalDate.now();
            long daysDifference = ChronoUnit.DAYS.between(lastLoginLocalDate, currentDate);

            UserStreak userStreak = userStreakRepository.findByUserId(rUserStreak.getUserId())
                .orElseThrow(() -> new NotFoundException("Could not find UserStreak for user."));

            return switch ((int) daysDifference) {
                // if (daysDifference == 0), Do nothing. Maybe later add hello again message?
                case 0 -> RUserStreak.builder()
                    .userId(rUserStreak.getUserId())
                    .username(userStreak.getUser().getUsername())
                    .currentStreak(rUserStreak.getCurrentStreak())
                    .highestStreak(rUserStreak.getHighestStreak())
                    .lastLogin(rUserStreak.getLastLogin())
                    .build();
                // Streak must be incremented
                case 1 -> incrementUserStreak(userStreak);
                // Streak must be reset (edge case, if last login is after current time) Streak must be reset
                default -> resetUserStreak(userStreak);
            };
        }
        catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    protected RUserStreak incrementUserStreak(UserStreak userStreak) throws Exception {
        try {
            // Assume user streak increment operation is correct
            userStreak.setCurrentStreak(userStreak.getCurrentStreak() + 1);
            userStreak.setHighestStreak(
                userStreak.getHighestStreak() <= userStreak.getCurrentStreak()
                ? userStreak.getHighestStreak() + 1
                : userStreak.getHighestStreak()
            );
            userStreak.setLastLogin(DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime()));

            UserStreak streak = userStreakRepository.save(userStreak);

            return RUserStreak.builder()
                .userId(streak.getUserId())
                .username(streak.getUser().getUsername())
                .currentStreak(streak.getCurrentStreak())
                .highestStreak(streak.getHighestStreak())
                .lastLogin(streak.getLastLogin())
                .build();
        }
        catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    protected RUserStreak resetUserStreak(UserStreak userStreak) throws Exception {
        try {
            // Assume user streak reset operation is correct
            userStreak.setCurrentStreak(1);
            userStreak.setLastLogin(DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime()));

            UserStreak streak = userStreakRepository.save(userStreak);

            return RUserStreak.builder()
                .userId(streak.getUserId())
                .username(streak.getUser().getUsername())
                .currentStreak(streak.getCurrentStreak())
                .highestStreak(streak.getHighestStreak())
                .lastLogin(streak.getLastLogin())
                .build();
        }
        catch (Exception e) {
            throw e;
        }
    }

    public RUserStreak getUserStreakByToken(String email) throws Exception {
        try {
            return getUserStreakWithEmail(email);
        }
        catch (NotFoundException e1) {
            throw e1;
        }
        catch (Exception e2) {
            System.out.println("ERROR: Could not fetch UserStreak.");
            throw e2;
        }
    }

    private RUserStreak getUserStreakWithEmail(String email) throws Exception {
        try {
            Optional<UserStreak> optionalStreak = userStreakRepository.findByUserEmail(email);

            if (optionalStreak.isEmpty()) {
                throw new NotFoundException("UserStreak does not exist for given email: [" + email + "].");
            }

            UserStreak streak = optionalStreak.get();

            return RUserStreak.builder()
                .userId(streak.getUserId())
                .username(streak.getUser().getUsername())
                .currentStreak(streak.getCurrentStreak())
                .highestStreak(streak.getHighestStreak())
                .lastLogin(streak.getLastLogin())
                .build();
        }
        catch (NotFoundException e1) {
            throw e1;
        }
        catch (Exception e2) {
            System.out.println("ERROR: Could not fetch UserStreak.");
            throw e2;
        }
    }

    public Boolean createUserStreak(String email) throws Exception {
        User user = accountRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        return createUserStreak(user);
    }

    public Boolean createUserStreak(User user) throws Exception {
        try {
            if (userStreakRepository.findByUserEmail(user.getEmail()).isPresent()) {
                return false; // UserStreak already exists
            }

            // Otherwise, create new "blank" UserStreak
            UserStreak newUserStreak = new UserStreak();
            newUserStreak.setCurrentStreak(1);
            newUserStreak.setHighestStreak(1);
            newUserStreak.setLastLogin(DateUtils.convertUtilDateToSqlDate(Calendar.getInstance().getTime()));
            newUserStreak.setUser(user);

            userStreakRepository.save(newUserStreak);
            return true;
        }
        catch (Exception e) {
            System.out.println("ERROR: Could not create UserStreak.");
            throw e;
        }
    }

    public List<RUserStreak> getAllUserStreaks() throws Exception {
        try {
            return userStreakRepository.findAll().stream()
                .map(streak -> new RUserStreak(streak.getUserId(), streak.getUser().getUsername(), streak.getCurrentStreak(), streak.getHighestStreak(), streak.getLastLogin()))
                .collect(Collectors.toList());
        }
        catch (Exception e) {
            System.out.println("ERROR: Could not fetch all UserStreaks.");
            throw e;
        }
    }

}
