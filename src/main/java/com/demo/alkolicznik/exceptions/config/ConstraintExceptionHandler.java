package com.demo.alkolicznik.exceptions.config;

import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.utils.ExceptionUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.money.UnknownCurrencyException;

@RestControllerAdvice
public class ConstraintExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = ExceptionUtils.getMessage(e.getFieldErrors());
        String path = ExceptionUtils.getPath(request);
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiException> handleConstraintViolationException(ConstraintViolationException e,
                                                                           WebRequest request) {
        String message = ExceptionUtils.getMessage(e.getConstraintViolations());
        String path = ExceptionUtils.getPath(request);
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UnknownCurrencyException.class)
    public ResponseEntity<ApiException> handleUnknownCurrencyException(UnknownCurrencyException e, WebRequest request) {
        String message = "You have left currency unit empty in application's properties";
        String path = ExceptionUtils.getPath(request);
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }
}
