package app.linguistai.bmvp.controller;

import java.util.List;

import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.request.QResetPassword;
import app.linguistai.bmvp.request.QResetPasswordVerification;
import app.linguistai.bmvp.request.QResetPasswordSave;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.exception.ExceptionLogger;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.request.QChangePassword;
import app.linguistai.bmvp.request.QUserLogin;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.response.RLoginUser;
import app.linguistai.bmvp.response.RRefreshToken;
import app.linguistai.bmvp.response.RUserProfile;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.AccountService;
import app.linguistai.bmvp.service.EmailService;
import app.linguistai.bmvp.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("profile")
public class ProfileController {
    private final ProfileService profileService;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody QUserProfile profile,
        @RequestHeader(Header.USER_EMAIL) String email) {

        try {
            RUserProfile userProfile = profileService.updateUserProfile(email, profile);
            return Response.create("Profile is updated", HttpStatus.OK, userProfile);
        } catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.BAD_REQUEST);
        }        
    }

    @GetMapping("")
    public ResponseEntity<Object> getProfile(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            RUserProfile userProfile = profileService.getUserProfile(email);
            return Response.create("OK", HttpStatus.OK, userProfile);
        } catch (Exception e) {
            return Response.create(ExceptionLogger.log(e), HttpStatus.BAD_REQUEST);
        }        
    }

}