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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@PropertySource("classpath:imageKit.properties")
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

    public String scaleTransformation(String fileId, int height, int width) {
        Map<String, Object> options = getBaseOptionsForMapping(fileId);
        options.put("transformation", List.of(Map.of("height", String.valueOf(height),
                "width", String.valueOf(width))));
        return this.mapUrl(options);
    }

    public String namedTransformation(String fileId, String namedTransformation) {
        Map<String, Object> options = getBaseOptionsForMapping(fileId);
        options.put("transformation", List.of(Map.of("named", namedTransformation)));
        return this.mapUrl(options);
    }

    private Map<String, Object> getBaseOptionsForMapping(String fileId) {
        String remotePath = getFilePath(fileId);
        long updatedAt = getUpdatedAt(fileId);

        Map<String, Object> baseOptions = new HashMap<>();
        baseOptions.put("path", remotePath);
        baseOptions.put("queryParameters", Map.of("updatedAt", String.valueOf(updatedAt)));
        return baseOptions;
    }

    @SneakyThrows
    public long getUpdatedAt(String fileId) {
        Result result = ImageKit.getInstance().getFileDetail(fileId);
        return result.getUpdatedAt().toInstant().getEpochSecond();
    }

    @SneakyThrows
    public String getFilePath(String fileId) {
        Result result = ImageKit.getInstance().getFileDetail(fileId);
        return result.getFilePath();
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
    private String mapUrl(Map<String, Object> options) {
        return imageKit.getUrl(options);
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

    private void setConfig(String endpoint, String publicKey, String privateKey) {
        imageKit.setConfig(new Configuration(publicKey, privateKey, endpoint));
    }
}
