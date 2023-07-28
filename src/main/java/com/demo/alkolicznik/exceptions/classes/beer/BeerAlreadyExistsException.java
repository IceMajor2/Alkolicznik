package com.demo.alkolicznik.exceptions.classes.beer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Beer already exists")
public class BeerAlreadyExistsException extends RuntimeException {

    public BeerAlreadyExistsException() {
        super("Beer already exists");
    }
}
