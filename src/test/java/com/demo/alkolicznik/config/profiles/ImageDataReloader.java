package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.FileUtils;
import com.demo.alkolicznik.utils.Utils;
import com.demo.alkolicznik.utils.mappers.ResultSetExtractors;
import com.demo.alkolicznik.utils.mappers.RowMappers;
import com.demo.alkolicznik.utils.requests.ImageKitRequests;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.results.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.demo.alkolicznik.utils.FileUtils.getBufferedImageFromLocal;
import static com.demo.alkolicznik.utils.FileUtils.getRawPathToClassPathResource;
import static com.demo.alkolicznik.utils.requests.ImageKitRequests.getDirectory;
import static com.demo.alkolicznik.utils.requests.ImageKitRequests.getUpdatedAt;

@Component
@Profile("image")
@PropertySources({ // order is meaningful: first is the main .properties loaded, then test's overrides duplicates
        @PropertySource("classpath:imageKit.properties"),
        @PropertySource("classpath:profiles/image.properties")
})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ImageDataReloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDataReloader.class);

    private JdbcTemplate jdbcTemplate;
    @Value("${imageKit.path}")
    private String imageKitPath;
    @Value("classpath:data_img/store")
    private Resource storeImageDir;
    @Value("classpath:data_img/beer")
    private Resource beerImageDir;

    @Autowired
    public ImageDataReloader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void reload() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException, IOException {
        String remoteBeerImgPath = imageKitPath + "/beer";
        String remoteStoreImgPath = imageKitPath + "/store";
        LOGGER.info("Reloading ImageKit's directory...");
        LOGGER.info("Deleting unwanted images from '%s' directory...".formatted(remoteBeerImgPath));
        List<String> beerImgFilenames = FileUtils.convertToFilenamesList(beerImageDir.getFile().listFiles());
        deleteFilesRemote(remoteBeerImgPath, false, beerImgFilenames);

        LOGGER.info("Deleting unwanted images from '%s' directory...".formatted(remoteStoreImgPath));
        deleteFilesRemote(remoteStoreImgPath, true, null);

        LOGGER.info("Sending BEER images to remote directory '%s'...".formatted(remoteBeerImgPath));
        String srcPath = getRawPathToClassPathResource("/data_img/beer");
        sendImagesRemote(srcPath, remoteBeerImgPath, BeerImage.class);

        LOGGER.info("Sending STORE images to remote directory '%s'...".formatted(remoteStoreImgPath));
        srcPath = getRawPathToClassPathResource("/data_img/store");
        sendImagesRemote(srcPath, remoteStoreImgPath, StoreImage.class);

        LOGGER.info("Updating image tables in the database...");
        List<BaseFile> beerURLs = getDirectory(remoteBeerImgPath);
        List<BaseFile> storeURLs = getDirectory(remoteStoreImgPath);
        updateDatabaseWithImages(beerURLs, BeerImage.class);
        updateDatabaseWithImages(storeURLs, StoreImage.class);
    }

    private void sendImagesRemote(String srcPath, String remotePath, Class<? extends ImageModel> imgClass) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        if (Utils.isStoreImage(imgClass)) {
            List<String> names = getStoreNames();
            sendStoreImagesRemote(srcPath, remotePath, names);
        } else if (Utils.isBeerImage(imgClass)) {
            List<Long> beerIDs = getBeerIDs();
            sendBeerImagesRemote(srcPath, remotePath, beerIDs);
        }
    }

    private void sendBeerImagesRemote(String srcPath, String remotePath, List<Long> beerIDs) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        File[] imagesDir = new File(srcPath).listFiles();
        for (File file : imagesDir) {
            if (getBufferedImageFromLocal(file.getAbsolutePath()) == null) {
                LOGGER.warn("File '{}' is not an image. It was not initialized", file.getName());
                return;
            } else if (!isValidBeerImageFilename(beerIDs, file.getName())) {
                LOGGER.warn("Beer image ('{}') was not loaded. Possible reasons:\n1) Failed to determine beer ID " +
                        "from filename: ID needs to be specified as first characters in the filename\n" +
                        "2) There is no beer of specified id", file.getName());
                return;
            }
            Result result = ImageKitRequests.upload(file.getAbsolutePath(), remotePath);
            if (result != null) {
                LOGGER.info("'%s' was successfully sent (ID: %s)"
                        .formatted(result.getName(), result.getFileId()));
            }
        }
    }

    private void sendStoreImagesRemote(String srcPath, String remotePath, List<String> names) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        File[] imagesDir = new File(srcPath).listFiles();
        for (File file : imagesDir) {
            if (getBufferedImageFromLocal(file.getAbsolutePath()) == null) {
                LOGGER.warn("File '{}' is not an image. It was not initialized", file.getName());
                return;
            } else if (!isValidStoreImageFilename(names, file.getName())) {
                LOGGER.warn("Store image ('{}') was not loaded. Possible reasons:\n1) Failed to determine store name " +
                        "from filename: filename should only consist of exact store name (case insensitive)\n" +
                        "2) There is no store of specified name", file.getAbsolutePath());
                return;
            }
            Result result = ImageKitRequests.upload(file.getAbsolutePath(), remotePath);
            if (result != null) {
                LOGGER.info("'%s' was successfully sent (ID: %s)"
                        .formatted(result.getName(), result.getFileId()));
            }
        }
    }

    /**
     * Deletes all files in the ImageKit's directory.
     * You can choose to omit deleting some files by specifying a
     * boolean parameter and a list of filenames.
     *
     * @param path          remote path whose contents are to be deleted
     * @param deleteAll     if true, will empty the directory
     * @param omitFilenames list of filenames that are not to be deleted
     *                      (if {@code deleteAll} was true, then that is ignored)
     */
    private void deleteFilesRemote(String path, boolean deleteAll, List<String> omitFilenames) throws
            ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException {
        List<BaseFile> directory = getDirectory(path);
        for (BaseFile file : directory) {
            if (!deleteAll && (omitFilenames != null && (omitFilenames.contains(file.getName())))) {
                LOGGER.info("No need to DELETE. '%s' was found (ID: %s)"
                        .formatted(file.getName(), file.getFileId()));
                continue;
            }
            LOGGER.info("Deleting... '%s'"
                    .formatted(file.getName()));
            ImageKitRequests.delete(file.getFileId());
        }
    }

    private <T extends ImageModel> void updateDatabaseWithImages(List<BaseFile> files, Class<T> imgClass) {
        long storeID = 0;
        for (BaseFile file : files) {
            String remoteId = file.getFileId();
            String url = getUrlWithUpdatedAt(file.getUrl(), remoteId);
            String filename = file.getName();
            Object foreignKey = getAssociatedReference(filename, imgClass);

            if (foreignKey != null) {
                if (Utils.isBeerImage(imgClass)) {
                    persistBeerImage((Long) foreignKey, remoteId, url);
                } else if (Utils.isStoreImage(imgClass)) {
                    storeID++;
                    persistStoreImage(storeID, (String) foreignKey, url, remoteId);
                }
            }
        }
    }

    private Object getAssociatedReference(String filename, Class<? extends ImageModel> imgClass) {
        if (Utils.isBeerImage(imgClass)) {
            return Utils.getBeerIdFromFilename(filename);
        } else if (Utils.isStoreImage(imgClass)) {
            String rawStoreName = Utils.getRawStoreNameFromFilename(filename);
            String storeName = jdbcTemplate.queryForObject("SELECT DISTINCT name FROM store WHERE LOWER(name) = LOWER(?)",
                    String.class, rawStoreName);
            return storeName;
        }
        throw new RuntimeException("Class is not supported");
    }

    private String getUrlWithUpdatedAt(String url, String remoteId) {
        long updatedAt = getUpdatedAt(remoteId);
        return String.format("%s?updatedAt=?%d", url, updatedAt);
    }

    private static boolean isValidBeerImageFilename(List<Long> beerIDs, String filename) {
        Long beerId = Utils.getBeerIdFromFilename(filename);
        return beerId != null && beerIDs.contains(beerId);
    }

    private static boolean isValidStoreImageFilename(List<String> storeNames, String filename) {
        String rawStoreName = Utils.getRawStoreNameFromFilename(filename);
        return storeNames.stream()
                .map(String::toLowerCase)
                .toList()
                .contains(rawStoreName);
    }

    private List<Long> getBeerIDs() {
        return jdbcTemplate.queryForList("SELECT id FROM beer", Long.class);
    }

    private List<String> getStoreNames() {
        return jdbcTemplate.queryForList("SELECT DISTINCT name FROM store", String.class);
    }

    private void persistBeerImage(Long beerId, String remoteId, String url) {
        try {
            jdbcTemplate.update("INSERT INTO beer_image (BEER_ID, REMOTE_ID, URL) VALUES (?, ?, ?)",
                    beerId, remoteId, url);
        } catch (DuplicateKeyException e) {
            LOGGER.warn("You have duplicated IDs in '{}'. You were trying to add beer image of id={} more than once",
                    beerImageDir, beerId);
            return;
        }
        BeerImage image = (BeerImage) jdbcTemplate
                .query("SELECT * FROM beer_image WHERE beer_id = ?", ResultSetExtractors.BEER_IMAGE, beerId);
        LOGGER.info("Entity persisted: " +
                        "BeerImage{beer_id={}, remote_id={}, image_url={}, image_component={}}",
                image.getId(), image.getRemoteId(), image.getImageUrl(), image.getImageComponent());
    }

    private void persistStoreImage(Long id, String storeName, String url, String remoteId) {
        jdbcTemplate.update("INSERT INTO store_image (ID, STORE_NAME, URL, REMOTE_ID) VALUES (?, ?, ?, ?)",
                id, storeName, url, remoteId);
        StoreImage image = (StoreImage) jdbcTemplate.queryForObject
                ("SELECT * FROM store_image WHERE store_name = ?", RowMappers.STORE_IMAGE, storeName);
        LOGGER.info("Entity persisted: " +
                        "StoreImage{id={}, store_name={}, remote_id={}, image_url={}, image_component={}}",
                image.getId(), image.getStoreName(), image.getRemoteId(), image.getImageUrl(), image.getImageComponent());
    }
}
