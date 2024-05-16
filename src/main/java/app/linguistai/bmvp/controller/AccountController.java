package app.linguistai.bmvp.controller;

import java.util.List;

import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.request.QResetPassword;
import app.linguistai.bmvp.request.QResetPasswordVerification;
import app.linguistai.bmvp.request.QUser;
import app.linguistai.bmvp.request.QResetPasswordSave;
import app.linguistai.bmvp.service.currency.ITransactionService;
import app.linguistai.bmvp.service.gamification.UserStreakService;
import app.linguistai.bmvp.service.stats.UserLoggedDateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final UserLoggedDateService userLoggedDateService;
    private final UserStreakService userStreakService;
    private final ITransactionService transactionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "login")
    public ResponseEntity<Object> login(@Valid @RequestBody QUserLogin userInfo) throws Exception {
        RLoginUser token = accountService.login(userInfo);
        return Response.create("Login is successful", HttpStatus.OK, token);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "register")
    public ResponseEntity<Object> register(@Valid @RequestBody QUser userInfo) throws Exception {
        RLoginUser ids = accountService.addUser(userInfo);
        return Response.create("Account is created", HttpStatus.OK, ids);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody QChangePassword userInfo,
        @RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        accountService.changePassword(email, userInfo);
        return Response.create("Password is changed", HttpStatus.OK);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/delete")
    public ResponseEntity<Object> deleteUser(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        accountService.deleteAccount(email);
        return Response.create("Account is deleted", HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) throws Exception {
        RRefreshToken newToken = accountService.refreshToken(auth);
        return Response.create("New access token is created", HttpStatus.OK, newToken);
    }

    @GetMapping("/test")
    public ResponseEntity<Object> testAuth(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            String test = "Welcome to the authenticated endpoint!";
            accountService.loginWithValidToken(email);
            return Response.create("ok", HttpStatus.OK, test);
        } catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/language")
    public ResponseEntity<Object> getUserLanguage(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("ok", HttpStatus.OK, accountService.getUserLanguage(email));
    }

    @PostMapping("/language/{language}")
    public ResponseEntity<Object> getUserLanguage(@RequestHeader(Header.USER_EMAIL) String email, @PathVariable String language) throws Exception {
        return Response.create("ok", HttpStatus.OK, accountService.setUserLanguage(email, language));
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

    @Operation(summary = "Validate Reset Password Token", description = "Validate the provided reset password token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validated password reset token"),
            @ApiResponse(responseCode = "400", description = "Invalid reset token"),
            @ApiResponse(responseCode = "404", description = "User or reset token not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/validate-reset")
    public ResponseEntity<Object> validateResetPassword(@Valid @RequestBody QResetPasswordVerification verificationInfo) throws Exception{
        accountService.validateResetCode(verificationInfo.getEmail(), verificationInfo.getResetCode(), false);
        return Response.create("Validated password reset token", HttpStatus.OK);
    }

    @Operation(summary = "Reset Password", description = "Reset the user's password using the provided reset password code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password is changed"),
            @ApiResponse(responseCode = "400", description = "Invalid reset token"),
            @ApiResponse(responseCode = "404", description = "User or reset token not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password")
    public ResponseEntity<Object> saveResetPassword(@Valid @RequestBody QResetPasswordSave passwordInfo) throws Exception{
        accountService.validateResetCode(passwordInfo.getEmail(), passwordInfo.getResetCode(), true);
        accountService.setPassword(passwordInfo.getEmail(), passwordInfo.getNewPassword());
        return Response.create("Password is changed", HttpStatus.OK);
    }
}
