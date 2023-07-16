package com.demo.alkolicznik.exceptions.classes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No property to update was specified")
public class PropertiesMissingException extends RuntimeException {

    public PropertiesMissingException() {
        super("No property to update was specified");
    }
}
