package com.demo.alkolicznik.exceptions.classes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EntitiesNotFoundException extends RuntimeException {
	
	public EntitiesNotFoundException(Long beerId, Long storeId) {
		super("Unable to find beer of '%d' id; Unable to find store of '%d' id"
				.formatted(beerId, storeId));
	}
	
	public EntitiesNotFoundException(Long beerId, String city) {
		super("No such city: '%s'; Unable to find beer of '%d' id"
				.formatted(city, beerId));
	}
}
