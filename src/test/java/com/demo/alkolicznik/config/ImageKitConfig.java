package com.demo.alkolicznik.config;

import com.demo.alkolicznik.utils.TestUtils;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.ResultList;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@Profile("image")
public class ImageKitConfig {

    @Autowired
    private ApplicationContext context;

    // TestUtils is autowired so that its beans are
    // made sure to load before ImageKitConfig's
    @Autowired
    private TestUtils testUtils;

    private ImageKit imageKit;

    private static final List<String> expectedBeerImages = List.of
            ("tyskie-gronie-0.65.png", "zubr-0.5.png",
                    "komes-porter-malinowy-0.33.jpg", "miloslaw-biale-0.5.jpg");

    @PostConstruct
    public void init() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
        setImageKit();
        deletePostedByTestImages();
        sendInitialImages();
    }

    private void deletePostedByTestImages() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath("/test/beer");
        ResultList resultList = ImageKit.getInstance().getFileList(getFileListRequest);

        for (BaseFile baseFile : resultList.getResults()) {
            if (expectedBeerImages.contains(baseFile.getName())) {
                continue;
            }
            this.imageKit.deleteFile(baseFile.getFileId());
        }
    }

    private void sendInitialImages() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        for (File file : new File(TestUtils.getRawPathToClassPathResource("/data_img/init_data")).listFiles()) {
            byte[] bytes = Files.readAllBytes(file.toPath());
            FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, file.getName());
            fileCreateRequest.setUseUniqueFileName(false);
            fileCreateRequest.setFolder("/test/beer");
            this.imageKit.upload(fileCreateRequest);
        }
    }

    private void setImageKit() {
        this.imageKit = ImageKit.getInstance();
        String endpoint = "https://ik.imagekit.io/icemajor";
        String publicKey = "public_YpQHYFb3+OX4R5aHScftYE0H0N8=";
        try {
            this.imageKit.setConfig(new io.imagekit.sdk.config.Configuration(publicKey,
                    Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
                    endpoint));
        } catch (IOException e) {
            throw new RuntimeException("Could not read secured file");
        }
    }

    static String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    static String getExternalId(String filename) {
        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setPath("/test/beer");
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
}
