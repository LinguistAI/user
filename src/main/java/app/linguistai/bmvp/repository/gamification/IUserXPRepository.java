package app.linguistai.bmvp.repository.gamification;

import app.linguistai.bmvp.model.gamification.UserXP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserXPRepository extends JpaRepository<UserXP, UUID> {
}
