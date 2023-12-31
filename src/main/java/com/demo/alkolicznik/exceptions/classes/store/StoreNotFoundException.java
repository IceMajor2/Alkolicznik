package com.demo.alkolicznik.exceptions.classes.store;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(Long id) {
        super("Unable to find store of '%d' id".formatted(id));
    }

    public StoreNotFoundException(String name, String city, String street) {
        super("Unable to find '%s' located in '%s, %s'".formatted(name, city, street));
    }

    public StoreNotFoundException(String name) {
        this(name, false);
    }

    public StoreNotFoundException(String name, boolean rawName) {
        super(rawName ? "Unable to find store of name similar to '%s'".formatted(name)
                : "Unable to find store of '%s' name".formatted(name));
    }
}
