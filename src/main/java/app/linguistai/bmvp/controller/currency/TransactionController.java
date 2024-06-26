package app.linguistai.bmvp.controller.currency;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.model.currency.UserGems;
import app.linguistai.bmvp.enums.TransactionType;
import app.linguistai.bmvp.request.currency.QTransactionRequest;
import app.linguistai.bmvp.response.Response;
import app.linguistai.bmvp.service.currency.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/transaction")
@Validated
public class TransactionController {
    private final ITransactionService transactionService;

    @PostMapping(path = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Process a transaction", description = "Processes a transaction for a user based on the specified type and amount")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transaction processed successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserGems.class))}
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient gems"),
        @ApiResponse(responseCode = "404", description = "User or gems not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> processTransaction(@RequestHeader(Header.USER_EMAIL) String email, @Valid @RequestBody QTransactionRequest request) throws Exception {
        return Response.create(
            "Transaction processed successfully",
            HttpStatus.OK,
            transactionService.processTransaction(email, request.getType(), request.getAmount())
        );
    }

    @GetMapping(path = "/types")
    @Operation(summary = "Get transaction types", description = "Retrieves all available transaction types")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Available transaction types",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionType[].class))}
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getTransactionTypes() {
        return Response.create("Available transaction types", HttpStatus.OK, TransactionType.values());
    }

    @GetMapping
    @Operation(summary = "Get User Gems", description = "Retrieves user's gem information")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user gems",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserGems.class))}
        ),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getUserGems(@RequestHeader(Header.USER_EMAIL) String email) throws Exception {
        return Response.create("Successfully retrieved user gems", HttpStatus.OK, transactionService.ensureUserGemsExists(email));
    }
}
