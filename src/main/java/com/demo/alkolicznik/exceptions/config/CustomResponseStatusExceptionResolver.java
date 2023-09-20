package com.demo.alkolicznik.exceptions.config;

import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beerprice.PriceIsSameException;
import com.demo.alkolicznik.exceptions.classes.city.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

@Component
public class CustomResponseStatusExceptionResolver extends ResponseStatusExceptionResolver {

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof BeerPriceAlreadyExistsException) return;
        if (ex instanceof BeerPriceNotFoundException) return;
        if (ex instanceof PriceIsSameException) return;
        if (ex instanceof BeerAlreadyExistsException) return;
        if (ex instanceof BeerNotFoundException) return;
        if (ex instanceof StoreAlreadyExistsException) return;
        if (ex instanceof StoreNotFoundException) return;
        if (ex instanceof EntitiesNotFoundException) return;
        if (ex instanceof NoSuchCityException) return;
        if (ex instanceof PropertiesMissingException) return;
        if (ex instanceof ObjectsAreEqualException) return;
        if (ex instanceof ImageNotFoundException) return;
        super.logException(ex, request);
    }
}
