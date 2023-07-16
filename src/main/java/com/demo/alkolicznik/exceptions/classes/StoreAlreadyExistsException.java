package com.demo.alkolicznik.exceptions.classes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Store already exists")
public class StoreAlreadyExistsException extends RuntimeException {

    public StoreAlreadyExistsException() {
        super("Store already exists");
    }
}
