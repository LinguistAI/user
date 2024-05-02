package app.linguistai.bmvp.service.stats;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.stats.UserLoggedDate;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.stats.IUserLoggedDateRepository;
import app.linguistai.bmvp.response.stats.RUserLoggedDate;
import app.linguistai.bmvp.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import app.linguistai.bmvp.consts.Parameter;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserLoggedDateService {

    private final IUserLoggedDateRepository userLoggedDateRepository;
    private final IAccountRepository accountRepository;

    public RUserLoggedDate getLoggedDates(String email, String sort, Integer numDays) throws Exception {
        try {
            // Check if the user exists
            User user = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            return getLoggedDates(user, sort, numDays);
        } catch (NotFoundException e) {
            log.error("Failed to get logged dates since user does not exist for email {}", email);
            throw e;
        }
    }

    public RUserLoggedDate getLoggedDates(UUID userId, String sort, Integer numDays) throws Exception {
        try {
            // Check if the user exists
            User user = accountRepository.findUserById(userId).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            return getLoggedDates(user, sort, numDays);
        } catch (NotFoundException e) {
            log.error("Failed to get logged dates since user does not exist for user id {}", userId);
            throw e;
        }
    }

    private RUserLoggedDate getLoggedDates(User user, String sort, Integer numDays) throws Exception {
        try {
            // Validate numDays
            if (numDays != null && numDays < 0) {
                throw new IllegalArgumentException("Number of days must be a non-negative integer, provided value: " + numDays);
            }

            // Calculate startDate if numDays is not null
            Date startDate = null;
            if (numDays != null) {
                startDate = Date.valueOf(LocalDate.now().minusDays(numDays));
            }

            // Retrieve user logged dates based on sort order and startDate
            List<UserLoggedDate> loggedDates;
            if (Parameter.ASCENDING_ORDER.equalsIgnoreCase(sort)) {
                loggedDates = (startDate != null) ?
                        userLoggedDateRepository.findByUserEmailAndLoggedDateGreaterThanEqualOrderByLoggedDateAsc(user.getEmail(), startDate) :
                        userLoggedDateRepository.findByUserEmailOrderByLoggedDateAsc(user.getEmail());
            } else { // Default is descending order
                loggedDates = (startDate != null) ?
                        userLoggedDateRepository.findByUserEmailAndLoggedDateGreaterThanEqualOrderByLoggedDateDesc(user.getEmail(), startDate) :
                        userLoggedDateRepository.findByUserEmailOrderByLoggedDateDesc(user.getEmail());
            }

            List<Date> dateList = loggedDates.stream()
                    .map(UserLoggedDate::getLoggedDate)
                    .collect(Collectors.toList());

            log.info("Retrieved logged dates for user {}", user.getEmail());

            return RUserLoggedDate.builder()
                    .loggedDates(dateList)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to get logged dates for user {}: {}", user.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to get logged dates for user {}", user.getEmail(), e);
            throw new SomethingWentWrongException();
        }
    }

    public void addLoggedDateByEmailAndDate(String email, java.util.Date loggedDate) throws Exception {
        try {
            User user = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            Optional<UserLoggedDate> existingLoggedDate = userLoggedDateRepository.findByUserAndLoggedDate(user,
                    DateUtils.convertUtilDateToSqlDate(loggedDate));

            if (existingLoggedDate.isEmpty()) {
                UserLoggedDate newUserLoggedDate = new UserLoggedDate(user, DateUtils.convertUtilDateToSqlDate(loggedDate));
                userLoggedDateRepository.save(newUserLoggedDate);
            }
        } catch (NotFoundException e) {
            log.error("Failed to add the logged date {} for user {} since user is not found.", loggedDate.toString(), email, e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to add the logged date {} for user {}", loggedDate.toString(), email, e);
            throw new SomethingWentWrongException();
        }
    }
}
