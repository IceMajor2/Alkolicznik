package com.demo.alkolicznik.exceptions.classes.image;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Store already has an image")
public class ImageAlreadyExistsException extends RuntimeException {
}
