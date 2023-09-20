package com.demo.alkolicznik.exceptions.classes.file;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY,
		reason = "Attached file is not an image")
public class FileIsNotImageException extends RuntimeException {
}
