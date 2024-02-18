package app.linguistai.bmvp.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.embedded.UserHobbyId;
import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserHobby;

@Repository
public interface IUserHobbyRepository extends JpaRepository<UserHobby, UserHobbyId> {
    List<Hobby> findAllHobbyByUserId(UUID id);
}