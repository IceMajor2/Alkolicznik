package com.demo.alkolicznik.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = getMessage(e.getFieldErrors());
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiException> handleConstraintViolationException(ConstraintViolationException e,
                                                                     WebRequest request) {
        String message = getMessage(e.getConstraintViolations());
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
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

    private String getMessage(Set<ConstraintViolation<?>> violations) {
        List<String> messages = new ArrayList<>();
        var iterator = violations.stream().iterator();
        while(iterator.hasNext()) {
            var violation = iterator.next();
            messages.add(violation.getMessage());
        }

        Collections.sort(messages);
        return String.join("; ", messages);
    }
}
