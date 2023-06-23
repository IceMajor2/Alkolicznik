package com.demo.alkolicznik.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Beer already exists")
public class BeerAlreadyExists extends RuntimeException{
}
