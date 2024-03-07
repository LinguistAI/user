package app.linguistai.bmvp.controller;

import java.util.List;

import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.request.QResetPassword;
import app.linguistai.bmvp.request.QResetPasswordVerification;
import app.linguistai.bmvp.request.QUser;
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
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.request.QChangePassword;
import app.linguistai.bmvp.request.QUserLogin;
import app.linguistai.bmvp.response.RLoginUser;
import app.linguistai.bmvp.response.RRefreshToken;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.AccountService;
import app.linguistai.bmvp.service.EmailService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("auth")
public class AccountController {
    private final AccountService accountService;
    private final EmailService emailService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "login")
    public ResponseEntity<Object> login(@Valid @RequestBody QUserLogin userInfo) throws Exception {
        RLoginUser token = accountService.login(userInfo);
        return Response.create("Login is successful", HttpStatus.OK, token);      
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "register")
    public ResponseEntity<Object> register(@Valid @RequestBody QUser userInfo) throws Exception {
        User ids = accountService.addUser(userInfo);
        return Response.create("Account is created", HttpStatus.OK, ids);
     
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody QChangePassword userInfo,
        @RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        accountService.changePassword(email, userInfo);
        return Response.create("Password is changed", HttpStatus.OK);       
    }

    @GetMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) throws Exception {
        RRefreshToken newToken = accountService.refreshToken(auth);
        return Response.create("New access token is created", HttpStatus.OK, newToken);      
    }

    @GetMapping("/test")
    public ResponseEntity<Object> testAuth() {
        String test = "Welcome to the authenticated endpoint!";
        return Response.create("ok", HttpStatus.OK, test);      
    }

    @GetMapping("/")
    public ResponseEntity<Object> getUsers() {
        List<User> userList = accountService.getUsers();
        return Response.create("Ok", HttpStatus.OK, userList);        
    }

    @GetMapping("hello")
    public ResponseEntity<Object> testUnsecuredEndpoint() {
        return Response.create("Ok", HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/request-reset")
    public ResponseEntity<Object> requestResetPassword(@Valid @RequestBody QResetPassword resetPasswordInfo) throws Exception {
        ResetToken resetToken = accountService.generateEmailToken(resetPasswordInfo.getEmail());
        emailService.sendPasswordResetEmail(resetPasswordInfo.getEmail(), resetToken);
        return Response.create("Password reset email is sent", HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/test-reset")
    public ResponseEntity<Object> requestResetPasswordWithoutEmail(@Valid @RequestBody QResetPassword resetPasswordInfo) throws Exception {
            ResetToken resetToken = accountService.generateEmailToken(resetPasswordInfo.getEmail());
            return Response.create("Reset token is generated", HttpStatus.OK, resetToken);
    }

    @PostMapping("/validate-reset")
    public ResponseEntity<Object> validateResetPassword(@Valid @RequestBody QResetPasswordVerification verificationInfo) {
        return Response.create("Validated password reset token", HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password")
    public ResponseEntity<Object> saveResetPassword(@Valid @RequestBody QResetPasswordSave passwordInfo) {
        return Response.create("Password is changed", HttpStatus.OK);
    }
}
