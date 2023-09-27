package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.FileUtils;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.demo.alkolicznik.utils.mappers.DatabaseTableConverters.convertToBeerImageList;
import static com.demo.alkolicznik.utils.mappers.DatabaseTableConverters.convertToStoreImageList;

@Configuration
@Profile("image")
@PropertySources({ // order is meaningful: first is the main .properties loaded, then test's overrides duplicates
        @PropertySource("classpath:imageKit.properties"),
        @PropertySource("classpath:profiles/image.properties")
})
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ImageProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProfile.class);

    private Environment env;
    private FileUtils fileUtils;
    private ImageDataReloader imageDataReloader;

    @Autowired
    public ImageProfile(FileUtils fileUtils, Environment env, ImageDataReloader imageDataReloader) {
        this.fileUtils = fileUtils;
        this.env = env;
        this.imageDataReloader = imageDataReloader;
        setImageKit();
    }

    @Bean("beerImages")
    @DependsOn("beers")
    public List<BeerImage> beerImages(List<Beer> beers) {
        LOGGER.info("Creating 'beerImages' bean...");
        String sql = "SELECT * FROM beer_image WHERE beer_id = ?";
        List<BeerImage> beerImages = convertToBeerImageList(sql, beers);
        return beerImages;
    }

    @Bean("storeImages")
    @DependsOn("stores")
    public List<StoreImage> storeImages(List<Store> stores) {
        LOGGER.info("Creating 'storeImages' bean...");
        String sql = "SELECT * FROM store_image";
        List<StoreImage> storeImages = convertToStoreImageList(sql, stores);
        return storeImages;
    }

    @Bean("pollIntervals")
    public int pollIntervals(Environment env) {
        return env.getProperty("imageKit.repeat-calls-in-ms", Integer.class);
    }

    @Bean("pollIntervalsUntil")
    public int pollIntervalsUntil(Environment env, int pollIntervals) {
        int tries = env.getProperty("imageKit.repeat-calls-tries", Integer.class);
        if (tries != 1) return tries * pollIntervals;
        return pollIntervals + 1;
    }

    @PostConstruct
    public void init() throws ForbiddenException, UnknownException, IOException, IllegalAccessException, InstantiationException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException {
        imageDataReloader.reload();
    }

    private void setImageKit() {
        checkRemotePathCollision();
        String publicKey = env.getProperty("imageKit.public-key");
        String privateKey = env.getProperty("imageKit.private-key");
        String endpoint = "https://ik.imagekit.io/%s".formatted(env.getProperty("imageKit.id"));
        ImageKit.getInstance().setConfig(new io.imagekit.sdk.config.Configuration(publicKey, privateKey, endpoint));
    }

    private void checkRemotePathCollision() {
        final String property = "imageKit.path";
        String mainPath = FileUtils.getRawPathToClassPathResource("imageKit.properties");
        String testPath = FileUtils.getRawPathToClassPathResource("profiles/image.properties");
        Properties mainImageKit = FileUtils.readPropertiesFile(mainPath);
        Properties testImageKit = FileUtils.readPropertiesFile(testPath);
        String mainImageKitPath = mainImageKit.getProperty(property);
        String testImageKitPath = testImageKit.getProperty(property);

        if (mainImageKitPath == null || mainImageKitPath.isBlank())
            throw new IllegalStateException("Property '%s' was missing from '%s'".formatted(property, mainPath));
        if (testImageKitPath == null || testImageKitPath.isBlank())
            throw new IllegalStateException("Property '%s' was missing from '%s'".formatted(property, testPath));
        if (Objects.equals(mainImageKitPath, testImageKitPath))
            throw new IllegalStateException("Property '%s' has the same value in production ('%s') and tests ('%s'). "
                    .formatted(property, mainPath, testPath) + "Please, make sure they differ");
    }
}
