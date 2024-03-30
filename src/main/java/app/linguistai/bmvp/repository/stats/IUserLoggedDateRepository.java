package app.linguistai.bmvp.repository.stats;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.stats.UserLoggedDate;
import app.linguistai.bmvp.model.embedded.UserLoggedDateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserLoggedDateRepository extends JpaRepository<UserLoggedDate, UserLoggedDateId> {

	Optional<UserLoggedDate> findByUserAndLoggedDate(User user, Date loggedDate);

	Optional<List<UserLoggedDate>> findByUserEmailOrderByLoggedDateDesc(String email);

	Optional<List<UserLoggedDate>> findByUserEmailOrderByLoggedDateAsc(String email);

	Optional<List<UserLoggedDate>> findByUserEmailAndLoggedDateGreaterThanEqualOrderByLoggedDateDesc(String email, Date startDate);

	Optional<List<UserLoggedDate>> findByUserEmailAndLoggedDateGreaterThanEqualOrderByLoggedDateAsc(String email, Date startDate);
}
