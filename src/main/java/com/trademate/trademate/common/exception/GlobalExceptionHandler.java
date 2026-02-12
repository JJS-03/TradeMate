package com.trademate.trademate.common.exception;

import com.trademate.trademate.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.trademate.trademate.common.exception.DuplicateEmailException;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1) DTO 검증 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "입력값이 올바르지 않습니다.",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2) 우리가 던진 예외(예: 이메일 중복)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {

        ErrorResponse response = new ErrorResponse(
                "BAD_REQUEST",
                e.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {

        ErrorResponse response = new ErrorResponse(
                "DUPLICATE_EMAIL",
                e.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {

        ErrorResponse response = new ErrorResponse(
                "INVALID_CREDENTIALS",
                e.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401
    }

}
