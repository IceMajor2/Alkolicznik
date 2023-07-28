package com.demo.alkolicznik.exceptions.classes.beerprice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BeerAndStoreNotFoundException extends RuntimeException {

	public BeerAndStoreNotFoundException(Long beerId, Long storeId) {
		super("Unable to find beer of '%d' id; Unable to find store of '%d' id"
				.formatted(beerId, storeId));
	}
}
