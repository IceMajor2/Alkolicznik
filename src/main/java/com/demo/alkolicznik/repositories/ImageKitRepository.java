package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.Utils;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.NotFoundException;
import io.imagekit.sdk.models.DeleteFolderRequest;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Repository
@PropertySource("classpath:imageKit.properties")
@Slf4j
public class ImageKitRepository {

    private ImageKit imageKit;
    private String imageKitPath;

    public ImageKitRepository(Environment env) {
        this.imageKit = ImageKit.getInstance();
        this.imageKitPath = env.getProperty("imageKit.path");

        String endpoint = env.getProperty("imageKit.endpoint");
        String publicKey = env.getProperty("imageKit.public-key");
        String privateKey = env.getProperty("imageKit.private-key");
        setConfig(endpoint, publicKey, privateKey);
    }

    private void setConfig(String endpoint, String publicKey, String privateKey) {
        imageKit.setConfig(new Configuration(publicKey, privateKey, endpoint));
    }

    /**
     * This procedure reads the file from a {@code path}, converts it into
     * an ImageKit-library-uploadable and sends it to an external image hosting.
     */
    @SneakyThrows
    public ImageModel save(String srcPath, String remotePath, String filename, Class<? extends ImageModel> imgClass) {
        Result result = upload(srcPath, remotePath, filename);
        String fileId = result.getFileId();
        // get link
        if (Utils.isStoreImage(imgClass))
            return new StoreImage(result.getUrl(), fileId);
        else if (Utils.isBeerImage(imgClass))
            return new BeerImage(result.getUrl(), fileId);
        return null;
    }

    @SneakyThrows
    public long getUpdatedAt(String fileId) {
        Result result = ImageKit.getInstance().getFileDetail(fileId);
        return result.getUpdatedAt().toInstant().getEpochSecond();
    }

    @SneakyThrows
    private Result upload(String srcPath, String remotePath, String filename) {
        // send to server
        byte[] bytes = Files.readAllBytes(Paths.get(srcPath));
        FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, filename);
        // prevent adding a random string to the end of the filename
        fileCreateRequest.setUseUniqueFileName(false);
        // set folder into which image will be uploaded
        fileCreateRequest.setFolder(imageKitPath + remotePath);
        return this.imageKit.upload(fileCreateRequest);
    }

    @SneakyThrows
    public void delete(ImageModel image) {
        imageKit.deleteFile(image.getRemoteId());
    }

    @SneakyThrows
    public void deleteFolder(String path) throws NotFoundException {
        DeleteFolderRequest deleteFolderRequest = new DeleteFolderRequest();
        deleteFolderRequest.setFolderPath(imageKitPath + path);
        imageKit.deleteFolder(deleteFolderRequest);
    }

    public static final class URLBuilder {

        private Map<String, Object> options = new HashMap<>();
        private static final String TRANSFORMATION_KEY = "transformation";
        private static final String PATH_KEY = "path";
        private static final String QUERY_PARAMS_KEY = "queryParameters";

        private URLBuilder() {}

        public static URLBuilder builder() {
            return new URLBuilder();
        }

        @SneakyThrows
        public URLBuilder defaultPath(String fileId) {
            String path = ImageKit.getInstance().getFileDetail(fileId).getFilePath();
            options.put(PATH_KEY, path);
            return this;
        }

        public URLBuilder updatedAt(long updatedAt) {
            options.put(QUERY_PARAMS_KEY, Map.of("updatedAt", String.valueOf(updatedAt)));
            return this;
        }

        public URLBuilder namedTransformation(String namedTransformation) {
            Map<String, String> currentTransformations = getTransformations();
            currentTransformations.put("named", namedTransformation);
            options.put(TRANSFORMATION_KEY, List.of(currentTransformations));
            return this;
        }

        public URLBuilder scaledTransformation(int height, int width) {
            Map<String, String> currentTransformations = getTransformations();
            currentTransformations.putAll(
                    Map.of("height", String.valueOf(height),
                            "width", String.valueOf(width)));
            options.put(TRANSFORMATION_KEY, List.of(currentTransformations));
            return this;
        }

        public URLBuilder cForce() {
            Map<String, String> currentTransformations = getTransformations();
            currentTransformations.put("c", "force");
            options.put(TRANSFORMATION_KEY, List.of(currentTransformations));
            return this;
        }

        public String build() {
            if (!options.containsKey(PATH_KEY)) {
                throw new IllegalStateException("Path must be specified");
            }
            if (!containsUpdatedAt()) {
                log.warn("Building image URL with 'updatedAt' parameter absent. " +
                        "It is very likely it may cause synchronization issues in the future");
            }
            return ImageKit.getInstance().getUrl(options);
        }

        private boolean containsUpdatedAt() {
            Map<String, String> queryParams = getQueryParams();
            return queryParams.containsKey("updatedAt");
        }

        private Map<String, String> getQueryParams() {
            return (Map<String, String>) options.getOrDefault(QUERY_PARAMS_KEY, Collections.EMPTY_MAP);
        }

        private Map<String, String> getTransformations() {
            List<Map<String, String>> chainedTransformations =
                    (List<Map<String, String>>) options
                            .getOrDefault(TRANSFORMATION_KEY, new ArrayList<Map<String, String>>());
            return chainedTransformations.isEmpty() ? new HashMap<>() : chainedTransformations.get(0);
        }
    }
}
