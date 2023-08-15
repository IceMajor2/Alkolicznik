package com.demo.alkolicznik.exceptions.classes.image;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ImageAlreadyExistsException extends RuntimeException {

	public ImageAlreadyExistsException(Class<? extends ImageModel> imgClass) {
		super(imgClass.equals(BeerImage.class)
				? "Beer already has an image"
				: imgClass.equals(StoreImage.class)
				? "Store already has an image"
				: "Image already exists");
	}
}
