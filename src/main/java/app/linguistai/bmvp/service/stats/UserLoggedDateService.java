package app.linguistai.bmvp.service.stats;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.stats.UserLoggedDate;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.stats.IUserLoggedDateRepository;
import app.linguistai.bmvp.response.stats.RUserLoggedDate;
import app.linguistai.bmvp.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserLoggedDateService {

    private final IUserLoggedDateRepository userLoggedDateRepository;
    private final IAccountRepository accountRepository;

    @Transactional
    public RUserLoggedDate getLoggedDates(String email, String sort, Integer numDays) {
        // Validate numDays
        if (numDays != null && numDays < 0) {
            throw new IllegalArgumentException("Number of days must be a non-negative integer");
        }

        // Calculate startDate if numDays is not null
        Date startDate = null;
        if (numDays != null) {
            startDate = Date.valueOf(LocalDate.now().minusDays(numDays));
        }

        // Retrieve user logged dates based on sort order and startDate
        List<UserLoggedDate> loggedDates;
        if ("asc".equalsIgnoreCase(sort)) {
            loggedDates = (startDate != null) ?
                    userLoggedDateRepository.findByUserEmailAndLoggedDateGreaterThanEqualOrderByLoggedDateAsc(email, startDate) :
                    userLoggedDateRepository.findByUserEmailOrderByLoggedDateAsc(email);
        } else { // Default is descending order
            loggedDates = (startDate != null) ?
                    userLoggedDateRepository.findByUserEmailAndLoggedDateGreaterThanEqualOrderByLoggedDateDesc(email, startDate) :
                    userLoggedDateRepository.findByUserEmailOrderByLoggedDateDesc(email);
        }

        List<Date> dateList = loggedDates.stream()
                .map(UserLoggedDate::getLoggedDate)
                .collect(Collectors.toList());

        return RUserLoggedDate.builder()
                .loggedDates(dateList)
                .build();
    }

    @Transactional
    public void addLoggedDateByEmailAndDate(String email, java.util.Date loggedDate) throws NotFoundException {
        User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        Optional<UserLoggedDate> existingLoggedDate = userLoggedDateRepository.findByUserAndLoggedDate(user,
                DateUtils.convertUtilDateToSqlDate(loggedDate));

        if (existingLoggedDate.isEmpty()) {
            UserLoggedDate newUserLoggedDate = new UserLoggedDate(user, DateUtils.convertUtilDateToSqlDate(loggedDate));
            userLoggedDateRepository.save(newUserLoggedDate);
        }
    }
}
