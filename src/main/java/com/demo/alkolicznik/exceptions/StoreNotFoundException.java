package com.demo.alkolicznik.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class StoreNotFoundException extends RuntimeException {
    
    public StoreNotFoundException(Long id) {
        super("Unable to find store of %d id".formatted(id));
    }
}
