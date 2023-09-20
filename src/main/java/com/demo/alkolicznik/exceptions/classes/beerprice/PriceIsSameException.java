package com.demo.alkolicznik.exceptions.classes.beerprice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PriceIsSameException extends RuntimeException {

	public PriceIsSameException(String priceString) {
		super("The price is '%s' already".formatted(priceString));
	}
}
