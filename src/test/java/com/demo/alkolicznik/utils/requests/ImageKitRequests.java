package com.demo.alkolicznik.utils.requests;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class ImageKitRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageKitRequests.class);

    private static String imageKitPath;

    @Autowired
    public void setImageKitPath(Environment env) {
        ImageKitRequests.imageKitPath = env.getProperty("imageKit.path");
    }

    public static List<BaseFile> getDirectory(String path) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath(path);
        return ImageKit.getInstance().getFileList(getFileListRequest).getResults();
    }

    public static Result upload(String srcPath, String remotePath) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        File toSend = new File(srcPath);
        List<String> baseFiles = getDirectory(remotePath).stream()
                .map(BaseFile::getName)
                .toList();
        if (baseFiles.contains(toSend.getName())) {
            return null;
        }
        return send(toSend, remotePath);
    }

    public static Result send(File file, String remotePath) throws IOException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, file.getName());
        fileCreateRequest.setUseUniqueFileName(false);
        fileCreateRequest.setFolder(remotePath);
        return ImageKit.getInstance().upload(fileCreateRequest);
    }

    public static void delete(String fileId) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        ImageKit.getInstance().deleteFile(fileId);
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
