package app.linguistai.bmvp.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.embedded.UserHobbyId;
import app.linguistai.bmvp.model.profile.UserHobby;

@Repository
public interface IUserHobbyRepository extends JpaRepository<UserHobby, UserHobbyId> {
    @Query("SELECT uh.hobby.name FROM UserHobby uh WHERE uh.user.id = :userId")
    List<String> findHobbiesByUserId(@Param("userId") UUID userId);

    void deleteByUserId(UUID id);
}