package com.sparta.spartaoutsourcing.user.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartaoutsourcing.user.controller.UserController;
import com.sparta.spartaoutsourcing.user.service.KakaoService;
import com.sparta.spartaoutsourcing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {UserController.class, UserService.class, KakaoService.class})
@RequiredArgsConstructor
public class UserExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler({UserException.class})
    public ResponseEntity<String> UserExceptionHandler(UserException ex) throws JsonProcessingException {
        Map<String, String> errorMap = Map.of(
                "message", ex.getMessage(),
                "error", ex.getCause().getMessage()
        );

        String errorInfo = objectMapper.writeValueAsString(errorMap);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                .body(errorInfo);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) throws JsonProcessingException {
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, List<Map<String, Object>>> groupError = ex.getBindingResult()
                                                            .getFieldErrors()
                                                            .stream()
                                                            .collect(Collectors.groupingBy(
                                                                    fieldError -> fieldError.getField(),
                                                                    Collectors.mapping(
                                                                    fieldError -> {
                                                                        String rejectedValue = (String) fieldError.getRejectedValue();

                                                                        return Map.of(
                                                                                "validation", fieldError.getDefaultMessage(),
                                                                                "value", rejectedValue == null ? "null" : rejectedValue
                                                                        );
                                                                    }, Collectors.toList())
                                                            ));

        groupError.forEach((field, fieldErrors) -> {
            if (fieldErrors.size() == 1) {
                errorMap.put(field, fieldErrors.get(0));
            } else {
                Map<Integer, Map<String, Object>> errors = new HashMap<>();

                for (int i = 0; i < fieldErrors.size(); i++) {
                    errors.put(i, fieldErrors.get(i));
                }
                errorMap.put(field, errors);
            }
        });


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                .body(objectMapper.writeValueAsString(errorMap));
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<String> NullPointerExceptionHandler(NullPointerException ex) throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                .body(objectMapper.writeValueAsString(ex));
    }

}
