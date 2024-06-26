package app.linguistai.bmvp.controller.gamification;

import java.util.List;

import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.request.QFriendRequest;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.gamification.FriendshipService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("friend")
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "request")
    public ResponseEntity<Object> sendFriendRequest(@RequestHeader(Header.USER_EMAIL) String email, @Valid @RequestBody QFriendRequest friendRequest) throws Exception {
        Friendship request = friendshipService.sendFriendRequest(email, friendRequest.getFriendId());
        return Response.create("Friend request is sent successfuly", HttpStatus.OK, request);       
    }

    @GetMapping("request")
    public ResponseEntity<Object> getFriendRequests(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        List<Friendship> friends = friendshipService.getFriendRequests(email);
        return Response.create("OK", HttpStatus.OK, friends);      
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "request/accept")
    public ResponseEntity<Object> acceptFriendRequest(@RequestHeader(Header.USER_EMAIL) String email, @Valid @RequestBody QFriendRequest friendRequest) throws Exception {
        Friendship request = friendshipService.acceptRequest(email, friendRequest.getFriendId());
        return Response.create("Friend request is accepted successfuly", HttpStatus.OK, request);      
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "request/reject")
    public ResponseEntity<Object> rejectFriendRequest(@RequestHeader(Header.USER_EMAIL) String email, @Valid @RequestBody QFriendRequest friendRequest) throws Exception {
        Friendship request = friendshipService.rejectRequest(email, friendRequest.getFriendId());
        return Response.create("Friend request is rejected successfuly", HttpStatus.OK, request);       
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> removeFriend(@RequestHeader(Header.USER_EMAIL) String email, @Valid @RequestBody QFriendRequest friendRequest) throws Exception {
        Friendship request = friendshipService.removeFriend(email, friendRequest.getFriendId());
        return Response.create("Friend is removed successfuly", HttpStatus.OK, request);        
    }

    @GetMapping
    public ResponseEntity<Object> getFriends(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        List<User> friends = friendshipService.getFriends(email);
        return Response.create("OK", HttpStatus.OK, friends);      
    }
}
