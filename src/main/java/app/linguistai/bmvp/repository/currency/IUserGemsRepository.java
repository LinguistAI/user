package app.linguistai.bmvp.repository.currency;

import app.linguistai.bmvp.model.currency.UserGems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IUserGemsRepository extends JpaRepository<UserGems, UUID> {}

