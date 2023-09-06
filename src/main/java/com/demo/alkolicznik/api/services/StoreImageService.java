package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import com.demo.alkolicznik.exceptions.classes.FileIsNotImageException;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageProportionsInvalidException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.StoreImageRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.demo.alkolicznik.utils.Utils.getBufferedImageFromLocal;
import static com.demo.alkolicznik.utils.Utils.getExtensionFromPath;

@Service
@AllArgsConstructor
public class StoreImageService {

    private StoreRepository storeRepository;

    private StoreImageRepository storeImageRepository;

    private ImageKitRepository imageKitRepository;

    public StoreImageResponseDTO get(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));
        StoreImage image = store.getImage()
                .orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
        return new StoreImageResponseDTO(image);
    }

    public StoreImageResponseDTO get(String storeName) {
        if (!storeRepository.existsByName(storeName))
            throw new StoreNotFoundException(storeName);
        StoreImage image = storeImageRepository.findByStoreName(storeName)
                .orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
        return new StoreImageResponseDTO(image);
    }

    public List<StoreImageResponseDTO> getAll() {
        return StoreImageResponseDTO.asList(storeImageRepository.findAll());
    }

    public StoreImageResponseDTO add(Store store, String imagePath) {
        StoreImage added = this.add(store.getName(), imagePath);
        store.setImage(added);
        return new StoreImageResponseDTO(added);
    }

    public StoreImageResponseDTO add(String storeName, ImageRequestDTO request) {
        if (!storeRepository.existsByName(storeName))
            throw new StoreNotFoundException(storeName);
        return new StoreImageResponseDTO(
                this.add(storeName, request.getImagePath()));
    }

    private StoreImage add(String storeName, String imagePath) {
        File file = new File(imagePath);
        fileCheck(file);

        if (storeImageRepository.existsByStoreName(storeName))
            throw new ImageAlreadyExistsException(StoreImage.class);
        StoreImage storeImage = (StoreImage) imageKitRepository.save(imagePath, "/store",
                createFilename(storeName, getExtensionFromPath(imagePath)), StoreImage.class);
        storeImage.setStoreName(storeName);

        // the addition of updatedAt key-value pair prevents from fetching
        // different image version from ImageKit API
        int[] newImageDimensions = GuiUtils.dimensionsForStoreImage(storeImage.getImageUrl() + "?updatedAt=1");
        String transformedUrl = imageKitRepository.scaleTransformation
                (storeImage.getRemoteId(), newImageDimensions[0], newImageDimensions[1]);
        storeImage.setImageUrl(transformedUrl);
        storeImage.setImageComponent();
        return storeImageRepository.save(storeImage);
    }

    public StoreImageResponseDTO update(String storeName, ImageRequestDTO request) {
        if (!storeRepository.existsByName(storeName))
            throw new StoreNotFoundException(storeName);
        StoreImage image = storeImageRepository.findByStoreName(storeName)
                .orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
        this.delete(image);
        return this.add(storeName, request);
    }

    public void delete(String storeName) {
        if (!storeRepository.existsByName(storeName))
            throw new StoreNotFoundException(storeName);
        StoreImage image = storeImageRepository.findByStoreName(storeName)
                .orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
        this.delete(image);
    }

    public void delete(StoreImage image) {
        imageKitRepository.delete(image);
        storeImageRepository.delete(image);
    }

    public Optional<StoreImage> findByStoreName(String name) {
        return storeImageRepository.findByStoreName(name);
    }

    private BufferedImage fileCheck(File file) {
        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
        BufferedImage image = getBufferedImageFromLocal(file.getAbsolutePath());
        if (image == null) throw new FileIsNotImageException();
        sizeCheck(image);
        return image;
    }

    private void sizeCheck(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        if (height * 3.5 < width) throw new ImageProportionsInvalidException("Image is too wide");
    }

    private String createFilename(String storeName, String extension) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder
                .append(storeName.toLowerCase().replace(' ', '-'))
                .append('.')
                .append(extension);
        return stringBuilder.toString();
    }
}
