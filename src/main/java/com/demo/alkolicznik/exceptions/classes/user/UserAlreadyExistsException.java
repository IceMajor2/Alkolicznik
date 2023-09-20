package com.demo.alkolicznik.exceptions.classes.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Username is already taken")
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("Username is already taken");
    }
}
