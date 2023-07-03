package com.demo.alkolicznik.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends RuntimeException {

    public BeerNotFoundException(Long id) {
        super("Unable to find beer of '%d' id".formatted(id));
    }

    public BeerNotFoundException(String fullname) {
        super("Unable to find beer of '%s' name".formatted(fullname));
    }

    public BeerNotFoundException(double volume) {
        super("Unable to find beer of '%d' volume".formatted(volume));
    }
}
