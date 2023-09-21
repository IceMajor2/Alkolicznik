package com.demo.alkolicznik.exceptions.classes.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.OK, reason = "Nothing changed - you were not logged in anyway")
public class NoLoggedInUserException extends RuntimeException {
}
