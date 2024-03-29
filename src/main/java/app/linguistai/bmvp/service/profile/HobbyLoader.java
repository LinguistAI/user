package app.linguistai.bmvp.service.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.repository.IHobbyRepository;

import java.util.Arrays;

@Component
public class HobbyLoader implements ApplicationRunner {

    private final IHobbyRepository hobbyRepository;

    @Autowired
    public HobbyLoader(IHobbyRepository hobbyRepository) {
        this.hobbyRepository = hobbyRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Hobby[] hobbies = {new Hobby("Reading"), new Hobby("Cooking"), new Hobby("Football"), new Hobby("Cinema"), new Hobby("Sports")};
        
        try {
            hobbyRepository.saveAll(Arrays.asList(hobbies));
        } catch (DataIntegrityViolationException e) {
            System.out.println("Hobbies already exist");
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
