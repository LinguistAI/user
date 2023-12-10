package app.linguistai.bmvp.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import app.linguistai.bmvp.response.Response;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // Handle 404 Not Found (Endpoint not found)
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        return Response.create("Endpoint not found", HttpStatus.NOT_FOUND);
    }

    // Handle 400 Bad Request (Invalid request body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        
        // Get validation errors
        List<String> validationErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> getFailedValidationMessage(fieldError))
            .collect(Collectors.toList());

        return Response.create("Request body is invalid!", HttpStatus.BAD_REQUEST, validationErrors);
    }

    // Handle 405 Method Not Allowed (Incorrect request type)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return Response.create("Incorrect request method!", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(CustomException e) {
        // Log the exception if needed
        return Response.create(ExceptionLogger.log(e), e.getStatus());
    }

    private String getFailedValidationMessage(FieldError fieldError) {
        // Extract the specific validation message that caused the failure
        String fieldName = fieldError.getField();
        String defaultMessage = fieldError.getDefaultMessage();

        StringBuilder errorMsg = new StringBuilder();

        if (Character.isLowerCase(defaultMessage.charAt(0))) {
            errorMsg.append(Character.toUpperCase(fieldName.charAt(0)));
            errorMsg.append(fieldName.substring(1));
            errorMsg.append(" ");
            errorMsg.append(defaultMessage);

        } else {
            errorMsg.append(defaultMessage);
        }

        return errorMsg.toString();
    }
}

