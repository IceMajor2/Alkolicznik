package com.demo.alkolicznik.exceptions.classes.beerprice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Store does not currently sell this beer")
public class BeerPriceNotFoundException extends RuntimeException {
}
