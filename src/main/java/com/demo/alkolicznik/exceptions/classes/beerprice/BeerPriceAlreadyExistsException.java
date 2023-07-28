package com.demo.alkolicznik.exceptions.classes.beerprice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Beer is already in store")
public class BeerPriceAlreadyExistsException extends RuntimeException {

    public BeerPriceAlreadyExistsException() {
        super("Beer is already in store");
    }
}
