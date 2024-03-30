package app.linguistai.bmvp.model.stats;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UserLoggedDateId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Entity
@Table(name = "user_logged_date")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserLoggedDateId.class)
public class UserLoggedDate {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private User user;

	@Id
	@Column(name = "logged_date", nullable = false)
	private Date loggedDate;
}
