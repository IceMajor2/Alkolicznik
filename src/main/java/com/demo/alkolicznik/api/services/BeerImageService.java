package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.exceptions.classes.FileIsNotImageException;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageProportionsInvalidException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.repositories.BeerImageRepository;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static com.demo.alkolicznik.utils.Utils.createBeerFilename;
import static com.demo.alkolicznik.utils.Utils.getBufferedImageFromLocal;

@Service
@AllArgsConstructor
public class BeerImageService {

    private BeerRepository beerRepository;

    private BeerImageRepository beerImageRepository;

    private ImageKitRepository imageKitRepository;

    public BeerImageResponseDTO get(Long beerId) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        BeerImage image = beer.getImage()
                .orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
        return new BeerImageResponseDTO(image);
    }

    public List<BeerImageResponseDTO> getAll() {
        return BeerImageResponseDTO.asList(beerImageRepository.findAll());
    }

    public BeerImageResponseDTO add(Long beerId, ImageRequestDTO request) {
        String imagePath = request.getImagePath();
        File file = new File(imagePath);
        fileCheck(file);
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        if (beerImageRepository.existsById(beerId))
            throw new ImageAlreadyExistsException(BeerImage.class);

        BeerImage beerImage = (BeerImage) imageKitRepository.save(imagePath, "/beer",
                createBeerFilename(beer.getFullName(), beer.getVolume(), imagePath), BeerImage.class);
        beer.setImage(beerImage);
        beerImage.setBeer(beer);
        String transformedUrl = imageKitRepository.namedTransformation
                (beerImage.getRemoteId(), "get_beer");
        beerImage.setImageUrl(transformedUrl);
        beerImage.setImageComponent();
        BeerImage saved = beerImageRepository.save(beerImage);
        return new BeerImageResponseDTO(saved);
    }

    public void delete(BeerImage image) {
        imageKitRepository.delete(image);
        image.getBeer().setImage(null);
        image.setBeer(null);
        beerImageRepository.deleteById(image.getId());
    }

    public void delete(Long beerId) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        BeerImage image = beer.getImage()
                .orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
        this.delete(image);
    }

    private void fileCheck(File file) {
        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
        BufferedImage image = getBufferedImageFromLocal(file.getAbsolutePath());
        if (image == null) throw new FileIsNotImageException();
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
}
