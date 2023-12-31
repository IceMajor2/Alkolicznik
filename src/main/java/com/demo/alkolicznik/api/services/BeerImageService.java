package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.exceptions.classes.file.FileIsNotImageException;
import com.demo.alkolicznik.exceptions.classes.file.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageProportionsInvalidException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.repositories.BeerImageRepository;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static com.demo.alkolicznik.utils.Utils.createBeerFilename;
import static com.demo.alkolicznik.utils.Utils.getBufferedImageFromLocal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerImageService {

    private static final int HEIGHT = 140;
    private static final int WIDTH = 45;

    private final BeerRepository beerRepository;
    private final BeerImageRepository beerImageRepository;
    private final ImageKitRepository imageKitRepository;

    @Transactional(readOnly = true)
    public BeerImageResponseDTO get(Long beerId) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        BeerImage image = beer.getImage()
                .orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
        return new BeerImageResponseDTO(image);
    }

    @Transactional(readOnly = true)
    public List<BeerImageResponseDTO> getAll() {
        return BeerImageResponseDTO.asList(beerImageRepository.findAll());
    }

    @Transactional
    public BeerImageResponseDTO add(Long beerId, ImageRequestDTO request) {
        String imagePath = request.getImagePath();
        File file = new File(imagePath);
        fileCheck(file);
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        if (beerImageRepository.existsById(beerId))
            throw new ImageAlreadyExistsException(BeerImage.class);

        BeerImage beerImage = (BeerImage) imageKitRepository.save(imagePath, "/beer",
                createBeerFilename(beer.getFullName(), beer.getVolume(), FilenameUtils.getExtension(file.getName())), BeerImage.class);
        beer.setImage(beerImage);
        beerImage.setBeer(beer);

        String transformedUrl = this.getURL(beerImage.getRemoteId());
        beerImage.setImageUrl(transformedUrl);
        beerImage.setImageComponent();
        BeerImageResponseDTO saved = new BeerImageResponseDTO(beerImageRepository.save(beerImage));
        log.info("Added: [{}]", saved);
        return saved;
    }

    @Transactional
    public BeerImageResponseDTO delete(BeerImage image) {
        BeerImageResponseDTO deleted = new BeerImageResponseDTO(image);
        imageKitRepository.delete(image);
        image.getBeer().setImage(null);
        image.setBeer(null);
        beerImageRepository.deleteById(image.getId());
        log.info("Deleted: [{}]", deleted);
        return deleted;
    }

    @Transactional
    public void delete(Long beerId) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException(beerId));
        BeerImage image = beer.getImage()
                .orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
        this.delete(image);
    }

    private String getURL(String fileId) {
        return ImageKitRepository.URLBuilder.builder()
                .defaultPath(fileId)
                .scaledTransformation(HEIGHT, WIDTH)
                .cForce()
                .updatedAt(imageKitRepository.getUpdatedAt(fileId))
                .build();
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
