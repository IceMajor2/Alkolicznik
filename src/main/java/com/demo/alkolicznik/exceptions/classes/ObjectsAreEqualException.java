package com.demo.alkolicznik.exceptions.classes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.OK, reason = "Objects are the same: nothing to update")
public class ObjectsAreEqualException extends RuntimeException {
}
