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
        Hobby[] hobbies = {
            new Hobby("Sports"),
            new Hobby("Reading"),
            new Hobby("Gardening"),
            new Hobby("Cooking"),
            new Hobby("Baking"),
            new Hobby("Photography"),
            new Hobby("Writing"),
            new Hobby("Drawing"),
            new Hobby("Painting"),
            new Hobby("Knitting"),
            new Hobby("Crocheting"),
            new Hobby("Fishing"),
            new Hobby("Hiking"),
            new Hobby("Cycling"),
            new Hobby("Bird Watching"),
            new Hobby("Astronomy"),
            new Hobby("Playing Musical Instruments"),
            new Hobby("Singing"),
            new Hobby("Dancing"),
            new Hobby("Yoga"),
            new Hobby("Meditation"),
            new Hobby("Collecting (e.g., stamps, coins, art)"),
            new Hobby("Model Building"),
            new Hobby("DIY Projects"),
            new Hobby("Video Gaming"),
            new Hobby("Board Gaming"),
            new Hobby("Puzzle Solving"),
            new Hobby("Traveling"),
            new Hobby("Learning New Languages"),
            new Hobby("Volunteering")
        };
        
        try {
            hobbyRepository.saveAll(Arrays.asList(hobbies));
        } catch (DataIntegrityViolationException e) {
            System.out.println("Hobbies already exist");
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
