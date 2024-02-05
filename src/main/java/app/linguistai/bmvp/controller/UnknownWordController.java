package app.linguistai.bmvp.controller;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.request.QAddUnknownWord;
import app.linguistai.bmvp.request.QCreateUnknownWordList;
import app.linguistai.bmvp.request.QUnknownWordListId;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.wordbank.IUnknownWordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("wordbank")
public class UnknownWordController {
    @Autowired
    private final IUnknownWordService unknownWordService;

    @PostMapping("/lists")
    public ResponseEntity<Object> createList(@Valid @RequestBody QCreateUnknownWordList qCreateUnknownWordList, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully created new list " + qCreateUnknownWordList.getTitle() + ".", HttpStatus.OK, unknownWordService.createList(qCreateUnknownWordList, email));
        }
        catch (Exception e) {
            return Response.create("Could not create new list " + qCreateUnknownWordList.getTitle() + ".", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-word")
    public ResponseEntity<Object> addWord(@Valid @RequestBody QAddUnknownWord qAddUnknownWord, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            unknownWordService.addWord(qAddUnknownWord, email);
            return Response.create("Successfully added unknown word " + qAddUnknownWord.getWord() + ".", HttpStatus.OK);
        }
        catch (RuntimeException e) {
            return Response.create(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return Response.create("Could not add unknown word " + qAddUnknownWord.getWord() + ".", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lists")
    public ResponseEntity<Object> getLists(@RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully retrieved all lists of user.", HttpStatus.OK, unknownWordService.getListsByEmail(email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/lists/activate")
    public ResponseEntity<Object> activateList(@Valid @RequestBody QUnknownWordListId qUnknownWordListId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully activated list.", HttpStatus.OK, unknownWordService.activateList(qUnknownWordListId.getListId(), email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/lists/deactivate")
    public ResponseEntity<Object> deactivateList(@Valid @RequestBody QUnknownWordListId qUnknownWordListId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully activated list.", HttpStatus.OK, unknownWordService.deactivateList(qUnknownWordListId.getListId(), email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
