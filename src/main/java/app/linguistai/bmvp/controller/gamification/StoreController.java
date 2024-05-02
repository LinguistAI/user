package app.linguistai.bmvp.controller.gamification;

import app.linguistai.bmvp.response.gamification.store.RQuizItems;
import app.linguistai.bmvp.response.gamification.store.RStoreItems;
import app.linguistai.bmvp.response.gamification.store.RUserItem;
import app.linguistai.bmvp.response.gamification.store.RUserItems;
import app.linguistai.bmvp.service.gamification.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.response.Response;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("store")
@Tag(name = "Store")
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/all")
    @Operation(summary = "Get all store items", description = "Retrieves all store items, enabled or disabled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all store items", content =
                    {@Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = RStoreItems.class)))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getAllStoreItems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws Exception {
        return Response.create("Successfully fetched all store items", HttpStatus.OK, storeService.getAllStoreItems(page, size));
    }

    @GetMapping("/all-enabled")
    @Operation(summary = "Get all enabled store items", description = "Retrieves all enabled store items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all enabled store items", content =
                    {@Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = RStoreItems.class)))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getAllEnabledStoreItems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws Exception {
        return Response.create("Successfully fetched all enabled store items", HttpStatus.OK, storeService.getAllEnabledStoreItems(page, size));
    }

    @GetMapping("/quiz-items")
    @Operation(summary = "Get all quiz related store/user items", description = "Retrieves all quiz related user items with quantities and gems. Includes enabled store items the user does not have, with quantity 0.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all quiz items", content =
                    {@Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = RQuizItems.class)))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getQuizItems(@RequestHeader(Header.USER_EMAIL) String email, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws Exception {
        return Response.create("Successfully fetched all quiz items", HttpStatus.OK, storeService.getAllQuizItems(email, page, size));
    }

    @GetMapping("/user-items")
    @Operation(summary = "Get user items", description = "Retrieves a user's items and their quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all user items", content =
                    {@Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = RUserItems.class)))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getUserItems(@RequestHeader(Header.USER_EMAIL) String email, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws Exception {
        return Response.create("Successfully fetched all user items", HttpStatus.OK, storeService.getUserItems(email, page, size));
    }

    @PostMapping("/purchase")
    @Operation(summary = "Purchase user item", description = "Purchases a user item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item purchased successfully", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = RUserItem.class))}),
            @ApiResponse(responseCode = "404", description = "User or store item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> purchaseUserItem(@RequestHeader(Header.USER_EMAIL) String email, @RequestParam @NotBlank UUID itemId) throws Exception {
        return Response.create("Item purchased successfully", HttpStatus.OK, storeService.purchaseUserItem(email, itemId));
    }

    @PostMapping("/user-items/decrease-quantity")
    @Operation(summary = "Decrease user item quantity by type", description = "Decreases the quantity of a user item by store item's type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User item quantity decreased successfully", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = RUserItem.class))}),
            @ApiResponse(responseCode = "404", description = "User, store item or the user item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> decreaseUserItemQuantityByType(@RequestHeader(Header.USER_EMAIL) String email, @RequestParam @NotBlank String type) throws Exception {
        return Response.create("User item quantity decreased successfully", HttpStatus.OK, storeService.decreaseUserItemQuantity(email, type));
    }
}
