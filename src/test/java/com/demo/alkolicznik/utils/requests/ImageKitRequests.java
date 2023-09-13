package com.demo.alkolicznik.utils.requests;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.demo.alkolicznik.utils.FileUtils.getRawPathToClassPathResource;

@Component
public class ImageKitRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageKitRequests.class);

    private static ImageKit imageKit = ImageKit.getInstance();
    private static String imageKitPath;

    @Autowired
    public void setImageKitPath(Environment env) {
        ImageKitRequests.imageKitPath = env.getProperty("imageKit.path");
    }

    /**
     * Deletes all files in an ImageKit's directory.
     * You can choose to omit deleting some files by specifying a
     * boolean parameter and a list of filenames.
     *
     * @param path          remote path whose contents are to be deleted
     * @param deleteAll     if true, will empty the directory
     * @param omitFilenames list of filenames that are not to be deleted
     *                      (if {@code deleteAll} was true, then it is ignored)
     */
    public static void deleteFilesIn(String path, boolean deleteAll, List<String> omitFilenames) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath(path);
        ResultList resultList = imageKit.getFileList(getFileListRequest);

        for (BaseFile baseFile : resultList.getResults()) {
            if (!deleteAll && (omitFilenames != null && (omitFilenames.contains(baseFile.getName())))) {
                LOGGER.info("No need to DELETE. '%s' was found (ID: %s)"
                        .formatted(baseFile.getName(), baseFile.getFileId()));
                continue;
            }
            LOGGER.info("Deleting... '%s'"
                    .formatted(baseFile.getName()));
            imageKit.deleteFile(baseFile.getFileId());
        }
    }

    public static void sendImages(String srcPath, String remotePath) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath(remotePath);
        Map<String, String> baseFiles = imageKit.getFileList(getFileListRequest)
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
            Result result = imageKit.upload(fileCreateRequest);
            LOGGER.info("'%s' was successfully sent (ID: %s)"
                    .formatted(result.getName(), result.getFileId()));
        }
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

    public static long getUpdatedAt(String fileId) {
        try {
            Result result = ImageKit.getInstance().getFileDetail(fileId);
            return result.getUpdatedAt().toInstant().getEpochSecond();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
