package com.demo.alkolicznik.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TestUtils {

	private static ResourceLoader resourceLoader;

	@Autowired
	public void setResourceLoader(ResourceLoader resourceLoader) {
		TestUtils.resourceLoader = resourceLoader;
	}

	public static Beer getBeer(Long beerId, List<Beer> beers) {
		for (Beer beer : beers) {
			if (beer.getId() == beerId) {
				return beer;
			}
		}
		return null;
	}

	public static Store getStore(Long storeId, List<Store> stores) {
		for (Store store : stores) {
			if (store.getId() == storeId) {
				return store;
			}
		}
		return null;
	}

	public static BeerPrice getBeerPrice(Long storeId, Long beerId, List<BeerPrice> prices) {
		for (var price : prices) {
			if (price.getBeer().getId().equals(beerId) && price.getStore().getId().equals(storeId)) {
				return price;
			}
		}
		return null;
	}

	public static List<Beer> getBeersInCity(String city, List<Beer> beers) {
		List<Beer> beersInCity = new ArrayList<>();

		one:
		for (Beer beer : beers) {
			for (BeerPrice beerPrice : beer.getPrices()) {
				if (beerPrice.getStore().getCity().equals(city)) {
					beersInCity.add(beer);
					continue one;
				}
			}
		}
		return beersInCity;
	}

	public static BeerImage getBeerImage(Long beerId, List<BeerImage> beerImages) {
		for (var image : beerImages) {
			if (image.getId().equals(beerId)) {
				return image;
			}
		}
		return null;
	}

	public static StoreImage getStoreImage(Long storeId, List<Store> stores) {
		for(var store: stores) {
			if(store.getId().equals(storeId)) {
				return store.getImage().orElse(null);
			}
		}
		return null;
	}

	public static StoreImage getStoreImage(String storeName, List<StoreImage> storeImages) {
		for(var image : storeImages) {
			if(image.getStoreName().equals(storeName)) {
				return image;
			}
		}
		return null;
	}

	public static User getUser(int id, List<User> users) {
		for (User user : users) {
			if (user.getId().intValue() == id) {
				return user;
			}
		}
		return null;
	}

	public static String buildURI(String uriString, Map<String, ?> parameters) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
		for (var entry : parameters.entrySet()) {
			builder
					.queryParam(entry.getKey(), entry.getValue());
		}
		String urlTemplate = builder.encode().toUriString();
		return urlTemplate;
	}

	public static BufferedImage getBufferedImageFromWeb(String url) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new URL(url));
			return image;
		}
		catch (IOException e) {
			return null;
		}
	}

	public static BufferedImage getBufferedImageFromLocal(String path) {
		try {
			BufferedImage image = ImageIO.read(new File(path));
			return image;
		}
		catch (IOException e) {
			return null;
		}
	}

	public static boolean imageEquals(BufferedImage imgA, BufferedImage imgB) {
		if(!dimensionsSame(imgA, imgB)) return false;
		int width = imgA.getWidth();
		int height = imgA.getHeight();
		// Loop over every pixel.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Compare the pixels for equality.
				if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean dimensionsSame(BufferedImage imgA, BufferedImage imgB) {
		return imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight();
	}

	public static String getRawPathToClassPathResource(String resource) {
		URI uri = null;
		try {
			uri = resourceLoader.getResource("classpath:" + resource).getURI();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return Paths.get(uri).toAbsolutePath().toString();
	}

	public static String getRawPathToImage(String imageFilename) {
		URI uri = null;
		try {
			uri = resourceLoader.getResource("classpath:data_img/" + imageFilename).getURI();
		}
		catch (IOException e) {
			try {
				uri = resourceLoader.getResource("classpath:data_img").getURI();
				return Paths.get(uri).toAbsolutePath().toString() + '/' + imageFilename;
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		String rawPath = Paths.get(uri).toAbsolutePath().toString();
		return rawPath;
	}
}
