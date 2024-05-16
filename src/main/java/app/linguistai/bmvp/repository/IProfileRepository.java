package app.linguistai.bmvp.repository;

import java.util.UUID;
import java.time.LocalDate;
import java.util.Optional;

import app.linguistai.bmvp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.linguistai.bmvp.model.profile.UserProfile;

@Repository
public interface IProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserEmail(String email);
    void deleteByUser(User user);
    @Transactional
    @Modifying
    @Query("UPDATE UserProfile u SET u.name = :name, u.birthDate = :birthDate, u.englishLevel = :englishLevel WHERE u.id = :id")
    UserProfile updateUserProfile(UUID id, String name, LocalDate birthDate, Integer englishLevel);
}
