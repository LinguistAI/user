package app.linguistai.bmvp.controller;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.request.wordbank.*;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.wordbank.IUnknownWordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.UUID;

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
    public ResponseEntity<Object> createList(@Valid @RequestBody QUnknownWordList qUnknownWordList, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully created new list " + qUnknownWordList.getTitle() + ".", HttpStatus.OK, unknownWordService.createList(qUnknownWordList, email));
        }
        catch (Exception e) {
            return Response.create("Could not create new list " + qUnknownWordList.getTitle() + ".", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/lists")
    public ResponseEntity<Object> editList(@Valid @RequestBody QEditUnknownWordList qEditUnknownWordList, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create(
                "Successfully edited list " + qEditUnknownWordList.getEditedList().getTitle() + ".",
                HttpStatus.OK,
                unknownWordService.editList(
                    qEditUnknownWordList.getListId(),
                    qEditUnknownWordList.getEditedList(),
                    email
                )
            );
        }
        catch (Exception e) {
            return Response.create("Could not edit list.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-word")
    public ResponseEntity<Object> addWord(@Valid @RequestBody QAddUnknownWord qAddUnknownWord, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully added unknown word " + qAddUnknownWord.getWord() + ".", HttpStatus.OK, unknownWordService.addWord(qAddUnknownWord, email));
        }
        catch (RuntimeException e) {
            return Response.create(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return Response.create("Could not add unknown word " + qAddUnknownWord.getWord() + ".", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/increase-confidence")
    public ResponseEntity<Object> increaseWordConfidence(@Valid @RequestBody QAddUnknownWord qAddUnknownWord, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully increased word confidence.", HttpStatus.OK, unknownWordService.increaseConfidence(qAddUnknownWord, email));
        }
        catch (Exception e) {
            return Response.create("Could not increase word confidence.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/decrease-confidence")
    public ResponseEntity<Object> decreaseWordConfidence(@Valid @RequestBody QAddUnknownWord qAddUnknownWord, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully decreased word confidence.", HttpStatus.OK, unknownWordService.decreaseConfidence(qAddUnknownWord, email));
        }
        catch (Exception e) {
            return Response.create("Could not decrease word confidence.", HttpStatus.INTERNAL_SERVER_ERROR);
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

    @GetMapping("/list/{listId}")
    public ResponseEntity<Object> getListWithWords(@Valid @PathVariable("listId") UUID listId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully retrieved list of user.", HttpStatus.OK, unknownWordService.getListWithWordsById(listId, email));
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

    @PostMapping("/lists/add-favorite")
    public ResponseEntity<Object> addFavoriteList(@Valid @RequestBody QUnknownWordListId qUnknownWordListId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully added list to favorites.", HttpStatus.OK, unknownWordService.addFavoriteList(qUnknownWordListId.getListId(), email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/lists/remove-favorite")
    public ResponseEntity<Object> removeFavoriteList(@Valid @RequestBody QUnknownWordListId qUnknownWordListId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully removed list from favorites.", HttpStatus.OK, unknownWordService.removeFavoriteList(qUnknownWordListId.getListId(), email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/lists/pin")
    public ResponseEntity<Object> pinList(@Valid @RequestBody QUnknownWordListId qUnknownWordListId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully pinned list.", HttpStatus.OK, unknownWordService.pinList(qUnknownWordListId.getListId(), email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/lists/unpin")
    public ResponseEntity<Object> unpinList(@Valid @RequestBody QUnknownWordListId qUnknownWordListId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully unpinned list.", HttpStatus.OK, unknownWordService.unpinList(qUnknownWordListId.getListId(), email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/list/{listId}")
    public ResponseEntity<Object> deleteList(@Valid @PathVariable("listId") UUID listId, @RequestHeader(Header.USER_EMAIL) String email) {
        try {
            return Response.create("Successfully deleted list.", HttpStatus.OK, unknownWordService.deleteList(listId, email));
        }
        catch (Exception e) {
            return Response.create(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
