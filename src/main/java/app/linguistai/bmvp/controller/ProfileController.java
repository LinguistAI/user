package app.linguistai.bmvp.controller;

import org.springframework.data.domain.Page;
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
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.request.QUserSearch;
import app.linguistai.bmvp.response.RUserProfile;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.AccountService;
import app.linguistai.bmvp.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("profile")
public class ProfileController {
    private final ProfileService profileService;
    private final AccountService accountService;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody QUserProfile profile,
        @RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        RUserProfile userProfile = profileService.updateUserProfile(email, profile);
        return Response.create("Profile is updated", HttpStatus.OK, userProfile);       
    }

    @GetMapping
    public ResponseEntity<Object> getProfile(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        RUserProfile userProfile = profileService.getUserProfile(email);
        return Response.create("OK", HttpStatus.OK, userProfile);        
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "search")
    public ResponseEntity<Object> searchUser(@Valid @RequestBody QUserSearch userSearch,
                                             @RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        Page<User> users = accountService.searchUser(userSearch, email);
        return Response.create("User search is successful", HttpStatus.OK, users);
    }
}
