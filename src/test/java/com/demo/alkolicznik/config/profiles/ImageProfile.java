package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.FileUtils;
import com.demo.alkolicznik.utils.mappers.DatabaseTableConverters;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.demo.alkolicznik.utils.requests.ImageKitRequests.*;

@Configuration
@Profile("image")
@PropertySource("classpath:profiles/image.properties")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ImageProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProfile.class);

    private JdbcTemplate jdbcTemplate;
    private FileUtils fileUtils;

    @Value("${imageKit.path}")
    private String imageKitPath;

    @Value("classpath:data_img/store_at_launch")
    private Resource storeImageDir;

    @Value("classpath:data_img/beer_at_launch")
    private Resource beerImageDir;

    @Autowired
    public ImageProfile(FileUtils fileUtils, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.fileUtils = fileUtils;
    }

    private void setImageKit() {
        ImageKit imageKit = ImageKit.getInstance();
        String endpoint = "https://ik.imagekit.io/alkolicznik";
        String publicKey = "public_9bnA9mQhgiGpder50E8rqIB98uM=";
        try {
            imageKit.setConfig(new io.imagekit.sdk.config.Configuration(publicKey,
                    Files.readAllLines(Paths.get("secure" + File.separator + "imagekit_private_key.txt")).get(0),
                    endpoint));
        } catch (IOException e) {
            throw new RuntimeException("Could not read file");
        }
    }

    @Bean("beerImages")
    @DependsOn("beers")
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
    public List<StoreImage> storeImages(List<Store> stores) {
        LOGGER.info("Creating 'storeImages' bean...");
        String sql = "SELECT * FROM store_image";
        List<StoreImage> storeImages = DatabaseTableConverters
                .convertToStoreImageList(sql, stores);
        updateStoreImageRemoteId(storeImages);
        updateUrlsWithUpdatedAt(storeImages, "store_image", "id");
        return storeImages;
    }

    @Bean("pollIntervals")
    public int pollIntervals(Environment env) {
        return env.getProperty("imageKit.repeat-calls-in-ms", Integer.class);
    }

    @Bean("pollIntervalsUntil")
    public int pollIntervalsUntil(Environment env, int pollIntervals) {
        return env.getProperty("imageKit.repeat-calls-tries", Integer.class) * pollIntervals;
    }

    @PostConstruct
    public void init() throws ForbiddenException, UnknownException, IOException, IllegalAccessException, InstantiationException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException {
        String remoteBeerImgPath = imageKitPath + "/beer";
        String remoteStoreImgPath = imageKitPath + "/store";
        LOGGER.info("Reloading ImageKit's directory...");
        setImageKit();

        LOGGER.info("Deleting unwanted images from '%s' directory...".formatted(remoteBeerImgPath));
        List<String> beerImgFilenames = FileUtils.convertToFilenamesList(beerImageDir.getFile().listFiles());
        deleteFilesIn(remoteBeerImgPath, false, beerImgFilenames);

        LOGGER.info("Deleting unwanted images from '%s' directory...".formatted(remoteStoreImgPath));
        //List<String> storeImgFilenames = FileUtils.convertToFilenamesList(storeImageDir.getFile().listFiles());
        deleteFilesIn(remoteStoreImgPath, true, null /*storeImgFilenames*/);

        LOGGER.info("Sending BEER images to remote directory '%s'...".formatted(remoteBeerImgPath));
        sendImages("/data_img/beer_at_launch", remoteBeerImgPath);
        LOGGER.info("Sending STORE images to remote directory '%s'...".formatted(remoteStoreImgPath));
        sendImages("/data_img/store_at_launch", remoteStoreImgPath);
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
}
