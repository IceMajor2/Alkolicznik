package com.demo.alkolicznik.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = getMessage(ex.getFieldErrors());
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    private String getMessage(List<FieldError> violations) {
        List<String> messages = new ArrayList<>();

        var iterator = violations.stream().iterator();

        while (iterator.hasNext()) {
            var violation = iterator.next();
            messages.add(violation.getDefaultMessage());
        }

        Collections.sort(messages);
        return String.join("; ", messages);
    }
}
