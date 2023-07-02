package com.demo.alkolicznik.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No such city")
public class NoSuchCityException extends RuntimeException {
}
