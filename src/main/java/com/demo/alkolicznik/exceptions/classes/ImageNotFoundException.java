package com.demo.alkolicznik.exceptions.classes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Unable to find image for this beer")
public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException() {
        super("Unable to find image for this beer");
    }
}
