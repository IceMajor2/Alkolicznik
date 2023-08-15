package com.demo.alkolicznik.api.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.exceptions.classes.FileIsNotImageException;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageProportionsInvalidException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.repositories.BeerImageRepository;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.vaadin.flow.component.html.Image;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import static com.demo.alkolicznik.utils.ModelDtoConverter.beerImageListToDtoList;
import static com.demo.alkolicznik.utils.Utils.getExtensionFromPath;
import static com.demo.alkolicznik.utils.Utils.getImage;

@Service
@AllArgsConstructor
public class BeerImageService {

	private BeerRepository beerRepository;

	private BeerImageRepository beerImageRepository;

	private ImageKitRepository imageKitRepository;

	public ImageModelResponseDTO get(Long beerId) {
		Beer beer = beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId));
		BeerImage image = beer.getImage()
				.orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
		return new ImageModelResponseDTO(image);
	}

	public List<ImageModelResponseDTO> getAll() {
		return beerImageListToDtoList(beerImageRepository.findAll());
	}

	public void add(Beer beer, String imagePath) throws IOException {
		File file = new File(imagePath);
		fileCheck(file);
		BeerImage beerImage = (BeerImage) imageKitRepository.save(imagePath, "/beer",
				createFilename(beer, getExtensionFromPath(imagePath)), BeerImage.class);
		beer.setImage(beerImage);
		beerImage.setBeer(beer);
		beerImageRepository.save(beerImage);
	}

	public ImageDeleteDTO delete(BeerImage image) {
		ImageDeleteDTO response = new ImageDeleteDTO(image.getBeer());
		imageKitRepository.delete(image);
		image.getBeer().setImage(null);
		image.setBeer(null);
		beerImageRepository.deleteById(image.getId());
		return response;
	}

	public ImageDeleteDTO delete(Long beerId) {
		Beer beer = beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId));
		BeerImage image = beer.getImage()
				.orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
		return this.delete(image);
	}

	public Image getComponent(Beer beer) {
		BeerImage image = beer.getImage()
				.orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
		Image component = image.getImageComponent();
		beerImageRepository.save(image);
		return component;
	}

	private void fileCheck(File file) {
		if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
		BufferedImage image = getImage(file);
		if(image == null) throw new FileIsNotImageException();
		if (!proportionsValid(image)) throw new ImageProportionsInvalidException();
	}

	private boolean proportionsValid(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		double widthScaled = (double) width / 3;
		double heightScaled = (double) height / 3;

		double heightExact = (widthScaled * 7) / 3;
		double heightVicinity = heightExact * 0.2;

		double lowerBound = heightVicinity;
		double upperBound = heightVicinity * 3;

		if (heightScaled >= heightExact - lowerBound && heightScaled <= heightExact + upperBound) {
			return true;
		}
		return false;
	}

	private String createFilename(Beer beer, String extension) {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder
				.append(beer.getFullName().toLowerCase().replace(' ', '-'))
				.append('-')
				.append(beer.getVolume())
				.append('.')
				.append(extension);
		return stringBuilder.toString();
	}
}
