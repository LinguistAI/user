package app.linguistai.bmvp.model.embedded;

import app.linguistai.bmvp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoggedDateId implements Serializable {
    private User user;
    private Date loggedDate;
}
