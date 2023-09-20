package com.demo.alkolicznik.exceptions.classes.city;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoSuchCityException extends RuntimeException {

    public NoSuchCityException(String city) {
        super("No such city: '%s'".formatted(city));
    }
}
