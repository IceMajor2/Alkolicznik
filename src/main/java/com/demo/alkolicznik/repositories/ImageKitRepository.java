package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.NotFoundException;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.DeleteFolderRequest;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ImageKitRepository {

    private ImageKit imageKit;

    private String imageKitPath;

    public ImageKitRepository(String imageKitPath) {
        this.imageKit = ImageKit.getInstance();
        this.imageKitPath = imageKitPath;
        setConfig();
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
        if (imgClass.equals(StoreImage.class)) {
            var baseOptions = getBaseOptionsForMapping(fileId);
            return new StoreImage(result.getUrl(), fileId);
        } else if (imgClass.equals(BeerImage.class)) {
            var baseOptions = getBaseOptionsForMapping(fileId);
            return new BeerImage(result.getUrl(), fileId);
        }
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
    public static long getUpdatedAt(String fileId) {
        Result result = ImageKit.getInstance().getFileDetail(fileId);
        return result.getUpdatedAt().toInstant().getEpochSecond();
    }

    @SneakyThrows
    public static String getFilePath(String fileId) {
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
    public List<BaseFile> findAllIn(String path) {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath(imageKitPath + path);
        return imageKit.getFileList(getFileListRequest).getResults();
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

    @SneakyThrows
    public Result getFileDetails(String fileId) {
        return imageKit.getFileDetail(fileId);
    }

    private void setConfig() {
        String endpoint = "https://ik.imagekit.io/alkolicznik";
        String publicKey = "public_9bnA9mQhgiGpder50E8rqIB98uM=";
        try {
            imageKit.setConfig(new Configuration(publicKey,
                    Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
                    endpoint));
        } catch (IOException e) {
            throw new RuntimeException("Could not read secured file");
        }
    }
}
