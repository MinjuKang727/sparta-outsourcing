package com.sparta.spartaoutsourcing.user.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserException.class})
    public ResponseEntity<Map<String, String>> UserExceptionHandler(UserException ex) {

        Map<String, String> errorMap = Map.of(
                "message", ex.getMessage(),
                "error", ex.getCause().getMessage()
        );

        return new ResponseEntity<>(
                errorMap,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        Map<String, Object> errorMap = new HashMap<>();

                ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .forEach(fieldError -> {
                    errorMap.put(
                            fieldError.getField(), Map.of(
                                    "validation", fieldError.getDefaultMessage(),
                                    "value", fieldError.getRejectedValue()
                            )
                    );
                });
        return new ResponseEntity<>(
                errorMap,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }
}
