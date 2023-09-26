package com.demo.alkolicznik.exceptions.config;

import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.utils.ExceptionUtils;
import com.demo.alkolicznik.utils.request.CookieUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiException> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        String message = "Could not log in: wrong credentials";
        String path = ExceptionUtils.getPath(request);
        ApiException error = new ApiException(HttpStatus.NOT_FOUND, message, path);
        return ResponseEntity.status(404).body(error);
    }

    @Component
    @Slf4j
    public static class ExceptionHandlerFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            try {
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                handleExpiredJwtException(request, response);
            } catch (SignatureException e) {
                handleSignatureException(request, response, e);
            } catch (UsernameNotFoundException e) {
                handleUsernameNotFoundException(request, response);
            }
        }

        private void handleUsernameNotFoundException(HttpServletRequest request, HttpServletResponse response) throws IOException {
            removeJWTCookie(request, response);
        }

        private void handleExpiredJwtException(HttpServletRequest request, HttpServletResponse response) throws IOException {
            removeJWTCookie(request, response);
        }

        private void handleSignatureException(HttpServletRequest request, HttpServletResponse response, SignatureException e) throws IOException {
            removeJWTCookie(request, response);
            log.warn("Suspicious JWT authentication request with invalid signature: %s".formatted(e.getMessage()));
        }

        private void removeJWTCookie(HttpServletRequest request, HttpServletResponse response) throws IOException {
            Cookie removeCookie = CookieUtils.createExpiredTokenCookie(request);
            response.addCookie(removeCookie);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            if (!ExceptionUtils.isCallToAPI(request)) {
                response.sendRedirect("/");
            }
        }
    }
}
