package com.digitinarytask.customer.exception;

import com.digitinarytask.customer.domain.entity.error.ErrorResponse;
import com.digitinarytask.customer.domain.enumeration.error.AccountErrorCode;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.domain.enumeration.error.OrganizationErrorCode;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle Customer Exception.
     */
    @ExceptionHandler({ CustomerException.class, AccountException.class, OrganizationException.class, AddressException.class })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Business logic error"),
        @ApiResponse(responseCode = "404", description = "Entity not found"),
        @ApiResponse(responseCode = "409", description = "Entity conflict")
    })
    public ResponseEntity<ErrorResponse> handleCustomerException(Exception ex) {
        ErrorResponse errorResponse = null;

        if (ex instanceof CustomerException customerException) {
            errorResponse = new ErrorResponse(LocalDateTime.now(), customerException.getErrorCode().getCode(), "Customer Error", customerException.getErrorCode().getMessage() + ": " + ex.getMessage());
        } else if (ex instanceof AccountException accountException) {
            errorResponse = new ErrorResponse(LocalDateTime.now(), accountException.getErrorCode().getCode(), "Account Error", accountException.getErrorCode().getMessage() + ": " + ex.getMessage());
        } else if (ex instanceof OrganizationException organizationException) {
            errorResponse = new ErrorResponse(LocalDateTime.now(), organizationException.getErrorCode().getCode(), "Organization Error", organizationException.getErrorCode().getMessage() + ": " + ex.getMessage());
        } else if (ex instanceof AddressException addressException) {
            errorResponse = new ErrorResponse(LocalDateTime.now(), addressException.getErrorCode().getCode(), "Address Error", addressException.getErrorCode().getMessage() + ": " + ex.getMessage());
        }

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }



    /**
     * Handle Data Integrity Violation Exception.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "409", description = "Data integrity violation")
    })
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String detailedMessage = "A data integrity violation occurred. This might be due to a constraint violation such as a duplicate key or a foreign key constraint.";
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Conflict", detailedMessage + " Details: " + ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle Entity Not Found Exception.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found", "Entity not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle Method Argument Not Valid Exception.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> {
                String field = error.getField().substring(error.getField().lastIndexOf('.') + 1);
                String message = error.getDefaultMessage();
                return field + ": " + message;
            })
            .reduce((first, second) -> first + ", " + second)
            .orElse("Validation error");
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle HTTP Message Not Readable Exception.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Malformed JSON request",
            "JSON parse error: " + ex.getMessage()
        );

        // if the cause was the customer type id, return a more specific error message
        if (ex.getCause() instanceof InvalidTypeIdException) {
            InvalidTypeIdException invalidTypeIdException = (InvalidTypeIdException) ex.getCause();
            errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Customer Type",
                "Invalid customer type provided: " + invalidTypeIdException.getTypeId()
            );
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Unsupported Operation",
            "An unsupported operation was attempted: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTypeIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTypeIdException(InvalidTypeIdException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Type ID",
            "An invalid type ID was provided: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Unsupported HTTP Method Exception.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "405", description = "Method not supported")
    })
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed", "Method not supported: " + ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Access Denied", "Access denied: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle General Exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
