package com.demo.alkolicznik.exceptions.classes.image;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Image proportions are invalid")
public class ImageProportionsInvalidException extends RuntimeException {

    public ImageProportionsInvalidException(String message) { super(message); }

    public ImageProportionsInvalidException() {
        super("Image proportions are invalid");
    }
}
