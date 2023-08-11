package com.demo.alkolicznik.exceptions.classes;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ImageNotFoundException extends RuntimeException {

	public ImageNotFoundException(Class<? extends ImageModel> clazz) {
		super(clazz.equals(BeerImage.class)
				? "Unable to find image for this beer"
				: clazz.equals(StoreImage.class)
				? "Unable to find image for this store"
				: "Unable to find image for this object");
	}
}
