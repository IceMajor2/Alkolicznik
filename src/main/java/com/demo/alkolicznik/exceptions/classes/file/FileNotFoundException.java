package com.demo.alkolicznik.exceptions.classes.file;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String path) {
        super("File was not found (Path: '%s')".formatted(path));
    }
}
