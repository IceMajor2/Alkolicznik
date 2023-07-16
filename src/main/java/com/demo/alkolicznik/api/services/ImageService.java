package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.responses.ImageModelResponseDTO;
import com.demo.alkolicznik.exceptions.classes.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.ImageModel;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageRepository;
import com.vaadin.flow.component.html.Image;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private BeerRepository beerRepository;
    private ImageRepository imageRepository;
    private ImageKit imageKit;

    public ImageService(ImageRepository imageRepository, BeerRepository beerRepository) {
        this.imageRepository = imageRepository;
        this.beerRepository = beerRepository;
        this.imageKit = ImageKit.getInstance();
        setConfig();
    }

    public ImageModelResponseDTO getBeerImageLink(Long beerId) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        ImageModel image = beer.getImage()
                .orElseThrow(() -> new ImageNotFoundException());
        return new ImageModelResponseDTO(image);
    }

    public Image getBeerImageComponent(Long beerId) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        ImageModel image = beer.getImage()
                .orElseThrow(() -> new ImageNotFoundException());

        if (image.getImageComponent() == null) {
            saveBeerImageComponent(beer);
        }
        return beer.getImage().get().getImageComponent();
    }

    private Image createImageComponent(ImageModel image) {
        Image imageComponent = new Image(image.getImageUrl(), "No image");
        return imageComponent;
    }

    private void saveBeerImageComponent(Beer beer) {
        ImageModel imageModel = beer.getImage().get();
        Image imageComponent = createImageComponent(imageModel);
        imageModel.setImageComponent(imageComponent);
        imageRepository.save(imageModel);
    }

    @SneakyThrows
    public Result uploadImage(String path) {
        BufferedImage image = ImageIO.read(new File(path));
        if (!areImageProportionsOk(image)) {
            throw new RuntimeException("Image's proportions are invalid");
        }
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, "sample_image.jpg");
        Result result = this.imageKit.upload(fileCreateRequest);
        return result;
    }

    private void setConfig() {
        String endpoint = "https://ik.imagekit.io/icemajor";
        String publicKey = "public_YpQHYFb3+OX4R5aHScftYE0H0N8=";
        try {
            imageKit.setConfig(new Configuration(publicKey,
                    Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
                    endpoint));
        } catch (IOException e) {
            throw new RuntimeException("Could not read secured file");
        }
    }

    private boolean areImageProportionsOk(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        double widthScaled = (double) width / 3;
        double heightScaled = (double) height / 3;

        double heightExact = (widthScaled * 7) / 3;
        double heightVicinity = heightExact * 0.2;

        if (heightScaled >= heightExact - heightVicinity && heightScaled <= heightExact + heightVicinity) {
            return true;
        }
        return false;
    }
}
