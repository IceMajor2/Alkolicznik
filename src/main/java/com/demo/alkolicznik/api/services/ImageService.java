package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.responses.ImageModelResponseDTO;
import com.demo.alkolicznik.exceptions.classes.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageProportionsInvalidException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * This procedure reads the file from a {@code path}, converts it into
     * an ImageKit-library-uploadable and sends it to an external image hosting.
     *
     * @param path path to an image
     * @return saved {@code ImageModel} entity
     */
    @SneakyThrows
    public ImageModel upload(String path, String filename) {
        // instantiate BufferedImage and check its proportions
        if (!areImageProportionsOk(ImageIO.read(new File(path)))) {
            throw new ImageProportionsInvalidException();
        }
        // send to server
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, filename);
        // prevent adding a random string to the end of the filename
        fileCreateRequest.setUseUniqueFileName(false);
        // set folder into which image will be uploaded
        fileCreateRequest.setFolder("/test/beer");

        Result result = this.imageKit.upload(fileCreateRequest);

        // get link with transformation 'get_beer'
        List<Map<String, String>> transformation = new ArrayList<>(List.of(Map.of("named", "get_beer")));
        Map<String, Object> options = new HashMap<>();
        options.put("path", result.getFilePath());
        options.put("transformation", transformation);

        return new ImageModel(imageKit.getUrl(options));
    }

    public ImageModel save(ImageModel imageModel) {
        return imageRepository.save(imageModel);
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

    public String createImageFilename(Beer beer, String extension) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder
                .append(beer.getFullName().toLowerCase().replace(' ', '-'))
                .append('-')
                .append(beer.getVolume())
                .append('.')
                .append(extension);
        return stringBuilder.toString();
    }

    public String extractExtensionFromPath(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }
}
