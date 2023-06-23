package com.demo.alkolicznik.exceptions;

import jakarta.validation.ConstraintViolation;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(IllegalArgumentException ex,
                                                                       WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "HAHAHA";
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ApiError error = new ApiError(status, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
       // return handleExceptionInternal(ex, error, headers, ex.getStatusCode(), request);
    }

    private String getMessage(Set<ConstraintViolation<?>> violations) {
        StringBuilder message = new StringBuilder("");
        var errorIterator = violations.stream().iterator();

        while (errorIterator.hasNext()) {
            var violation = errorIterator.next();
            message.append(violation.getMessage()).append("; ");
        }
        return message.delete(message.length() - 2, message.length()).toString();
    }
}
