package app.linguistai.bmvp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.response.RUserProfile;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("profile")
public class ProfileController {
    private final ProfileService profileService;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody QUserProfile profile,
        @RequestHeader(Header.USER_EMAIL) String email) {

        try {
            RUserProfile userProfile = profileService.updateUserProfile(email, profile);
            return Response.create("Profile is updated", HttpStatus.OK, userProfile);
        } catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.BAD_REQUEST);
        }        
    }

    @GetMapping
    public ResponseEntity<Object> getProfile(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            RUserProfile userProfile = profileService.getUserProfile(email);
            return Response.create("OK", HttpStatus.OK, userProfile);
        } catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.BAD_REQUEST);
        }        
    }

}
