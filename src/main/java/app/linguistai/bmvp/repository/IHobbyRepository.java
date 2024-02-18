package app.linguistai.bmvp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserProfile;

@Repository
public interface IHobbyRepository extends JpaRepository<Hobby, Integer> {
    Optional<UserProfile> findByNameIgnoreCase(String name);
}