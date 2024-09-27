package com.sosmoothocp.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
       ApiErrorResponse response = new ApiErrorResponse(
               HttpStatus.BAD_REQUEST.value(),
               "Validation Error",
               errorMap
       );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getStatusCode().value(),
                exception.getReason(),
                null
        );
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleFieldValidationException(FieldValidationException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(exception.getField(), exception.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMap
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenException(TokenException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getStatusCode().value(),
                exception.getReason(),
                null
        );
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(EmailNotConfirmedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailNotConfirmedException(EmailNotConfirmedException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getStatusCode().value(),
                exception.getReason(),
                null
        );
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailNotSentException(EmailNotSentException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getStatusCode().value(),
                exception.getReason(),
                null
        );
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

}
