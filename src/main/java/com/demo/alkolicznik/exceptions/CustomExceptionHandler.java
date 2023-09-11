package com.demo.alkolicznik.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = getMessage(e.getFieldErrors());
        String path = getPath(request);
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiException> handleConstraintViolationException(ConstraintViolationException e,
                                                                           WebRequest request) {
        String message = getMessage(e.getConstraintViolations());
        String path = getPath(request);
        ApiException error = new ApiException(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiException> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        String message = "Could not log in: wrong credentials";
        String path = getPath(request);
        ApiException error = new ApiException(HttpStatus.NOT_FOUND, message, path);
        return ResponseEntity.status(404).body(error);
    }

    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
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

    private static String getMessage(Set<ConstraintViolation<?>> violations) {
        List<String> messages = new ArrayList<>();
        var iterator = violations.stream().iterator();
        while (iterator.hasNext()) {
            var violation = iterator.next();
            messages.add(violation.getMessage());
        }

        Collections.sort(messages);
        return String.join("; ", messages);
    }

    @Component
    public static class ExceptionHandlerFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            try {
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                handleExpiredJwtException(request, response);
            }
        }

        private void handleExpiredJwtException(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String message = "Please log in again";
            String path = request.getServletPath();
            ApiException error = new ApiException(HttpStatus.UNAUTHORIZED, message, path);

            response.setStatus(error.getStatus());
            response.getWriter().write(convertObjectToJson(error));
        }

        private String convertObjectToJson(Object object) throws JsonProcessingException {
            if (object == null) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        }
    }
}
