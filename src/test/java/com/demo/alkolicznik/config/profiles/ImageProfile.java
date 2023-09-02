package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.TestUtils;
import com.demo.alkolicznik.utils.mappers.DatabaseTableConverters;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultList;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.demo.alkolicznik.utils.TestUtils.getRawPathToClassPathResource;

@Configuration
@Profile("image")
@PropertySource("classpath:profiles/image.properties")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ImageProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProfile.class);

    // TODO: replace with method
    private static final List<String> BEER_IMAGES = List.of(
            "tyskie-gronie-0.65.png", "zubr-0.5.png",
            "komes-porter-malinowy-0.33.jpg", "miloslaw-biale-0.5.jpg"
    );

    private static final List<String> STORE_IMAGES = List.of(
            "carrefour.png", "lubi.jpg", "abc.png"
    );

    private static String imageKitPath;

    private ImageKit imageKit;

    private JdbcTemplate jdbcTemplate;

    private TestUtils testUtils;

    @Autowired
    public ImageProfile(TestUtils testUtils, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.testUtils = testUtils;
    }

    @Autowired
    public void setImageKitPath(String imageKitPath) {
        ImageProfile.imageKitPath = imageKitPath;
    }

    @Bean("beerImages")
    @DependsOn("beers")
    @ConditionalOnProperty(prefix = "database.table.image", name = "enabled", havingValue = "true")
    public List<BeerImage> beerImages(List<Beer> beers) {
        LOGGER.info("Creating 'beerImages' bean...");
        String sql = "SELECT * FROM beer_image WHERE beer_id = ?";
        List<BeerImage> beerImages = DatabaseTableConverters
                .convertToBeerImageList(sql, beers);
        updateBeerImageRemoteId(beerImages);
        updateUrlsWithUpdatedAt(beerImages, "beer_image", "beer_id");
        return beerImages;
    }

    @Bean("storeImages")
    @DependsOn("stores")
    @ConditionalOnProperty(prefix = "database.table.image", name = "enabled", havingValue = "true")
    public List<StoreImage> storeImages(List<Store> stores) {
        LOGGER.info("Creating 'storeImages' bean...");
        String sql = "SELECT * FROM store_image";
        List<StoreImage> storeImages = DatabaseTableConverters
                .convertToStoreImageList(sql, stores);
        updateStoreImageRemoteId(storeImages);
        updateUrlsWithUpdatedAt(storeImages, "store_image", "id");
        return storeImages;
    }

    @PostConstruct
    public void init() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        String remoteBeerImgPath = imageKitPath + "/beer";
        String remoteStoreImgPath = imageKitPath + "/store";
        LOGGER.info("Reloading ImageKit's directory...");
        setImageKit();
        LOGGER.info("Deleting unwanted images from '%s' directory...".formatted(remoteBeerImgPath));
        deleteFilesIn(remoteBeerImgPath, false);
        LOGGER.info("Deleting unwanted images from '%s' directory...".formatted(remoteStoreImgPath));
        deleteFilesIn(remoteStoreImgPath, true);
        LOGGER.info("Sending BEER images to remote directory '%s'...".formatted(remoteBeerImgPath));
        sendImages("/data_img/beer_at_launch", remoteBeerImgPath);
        LOGGER.info("Sending STORE images to remote directory '%s'...".formatted(remoteStoreImgPath));
        sendImages("/data_img/store_at_launch", remoteStoreImgPath);
    }


    /**
     * Deletes all files in a remote, ImageKit's directory.
     * You can actually leave out test data (specified in a
     * static variable) by passing a boolean parameter.
     *
     * @param path           remote path that is to be deleted
     * @param deleteTestData if true, will delete everything
     *                       from directory - including test data
     */
    private void deleteFilesIn(String path, boolean deleteTestData) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath(path);
        ResultList resultList = this.imageKit.getFileList(getFileListRequest);

        for (BaseFile baseFile : resultList.getResults()) {
            if (!deleteTestData && (BEER_IMAGES.contains(baseFile.getName())
                    || STORE_IMAGES.contains(baseFile.getName()))) {
                LOGGER.info("No need to DELETE. '%s' was found (ID: %s)"
                        .formatted(baseFile.getName(), baseFile.getFileId()));
                continue;
            }
            LOGGER.info("Deleting... '%s'"
                    .formatted(baseFile.getName()));
            this.imageKit.deleteFile(baseFile.getFileId());
        }
    }

    private void sendImages(String srcPath, String remotePath) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath(remotePath);
        Map<String, String> baseFiles = this.imageKit.getFileList(getFileListRequest)
                .getResults()
                .stream()
                .collect(Collectors.toMap(BaseFile::getName, BaseFile::getFileId));
        File[] testDir =
                new File(getRawPathToClassPathResource(srcPath)).listFiles();

        for (File image : testDir) {
            if (baseFiles.containsKey(image.getName())) {
                continue;
            }
            byte[] bytes = Files.readAllBytes(image.toPath());
            FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, image.getName());
            fileCreateRequest.setUseUniqueFileName(false);
            fileCreateRequest.setFolder(remotePath);
            Result result = this.imageKit.upload(fileCreateRequest);
            LOGGER.info("'%s' was successfully sent (ID: %s)"
                    .formatted(result.getName(), result.getFileId()));
        }
    }

    private void setImageKit() {
        this.imageKit = ImageKit.getInstance();
        String endpoint = "https://ik.imagekit.io/alkolicznik";
        String publicKey = "public_9bnA9mQhgiGpder50E8rqIB98uM=";
        try {
            this.imageKit.setConfig(new io.imagekit.sdk.config.Configuration(publicKey,
                    Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
                    endpoint));
        } catch (IOException e) {
            throw new RuntimeException("Could not read secured file");
        }
    }

    public static String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static String getRemoteId(String filename, Class<? extends ImageModel> imgClass) {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        String remotePath = imageKitPath + (imgClass.equals(StoreImage.class)
                ? "/store" : imgClass.equals(BeerImage.class)
                ? "/beer" : null);
        getFileListRequest.setPath(remotePath);
        ResultList resultList = null;
        try {
            resultList = ImageKit.getInstance().getFileList(getFileListRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (BaseFile baseFile : resultList.getResults()) {
            if (baseFile.getName().equals(filename)) {
                return baseFile.getFileId();
            }
        }
        return null;
    }

    private void updateBeerImageRemoteId(List<BeerImage> beerImages) {
        LOGGER.info("Updating 'beer_image' table with remote IDs...");
        for (var image : beerImages) {
            String sql = "UPDATE beer_image SET remote_id = ? WHERE beer_id = ?";
            jdbcTemplate.update(sql, image.getRemoteId(), image.getId());
        }
    }

    private void updateStoreImageRemoteId(List<StoreImage> storeImages) {
        LOGGER.info("Updating 'store_image' table with remote IDs...");
        for (var image : storeImages) {
            String sql = "UPDATE store_image SET remote_id = ? WHERE store_name = ?";
            jdbcTemplate.update(sql, image.getRemoteId(), image.getStoreName());
        }
    }

    private void updateUrlsWithUpdatedAt(List<? extends ImageModel> images, String tableName, String idColumn) {
        LOGGER.info("Updating '%s' table with 'updatedAt' key...".formatted(tableName));
        for (var image : images) {
            long updatedAt = getUpdatedAt(image.getRemoteId());
            String newURL = image.getImageUrl() + "?updatedAt=" + updatedAt;
            String sql = "UPDATE %s m SET url = ? WHERE %s = ?".formatted(tableName, idColumn);
            jdbcTemplate.update(sql, newURL, image.getId());
            image.setImageUrl(newURL);
        }
    }

    private long getUpdatedAt(String fileId) {
        try {
            Result result = ImageKit.getInstance().getFileDetail(fileId);
            return result.getUpdatedAt().toInstant().getEpochSecond();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
