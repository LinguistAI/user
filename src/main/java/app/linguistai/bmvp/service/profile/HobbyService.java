package app.linguistai.bmvp.service.profile;

import java.util.ArrayList;
import java.util.List;
import app.linguistai.bmvp.repository.IUserHobbyRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserHobby;
import app.linguistai.bmvp.repository.IHobbyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class HobbyService {
    private final IUserHobbyRepository userHobbyRepository;
    private final IHobbyRepository hobbyRepository;

    @Transactional
    public List<String> updateUserHobby(User user, List<String> hobbies) throws Exception {
        try {
            // first delete the hobbies of the user
            userHobbyRepository.deleteByUserId(user.getId());

            List<Hobby> dbHobbies = hobbyRepository.findByNameInIgnoreCase(hobbies);
            List<UserHobby> userHobbies = new ArrayList<UserHobby>();

            for (Hobby h : dbHobbies) {
                userHobbies.add(new UserHobby(user, h));
            }

            // add user hobies to the db
            userHobbyRepository.saveAll(userHobbies);

            List<String> savedHobbies = userHobbyRepository.findHobbiesByUserId(user.getId());

            return savedHobbies;
        } catch (Exception e) {
            System.out.println("Something is wrong in update user hobbies");
            e.printStackTrace();
            throw e;
        }
    }

}
