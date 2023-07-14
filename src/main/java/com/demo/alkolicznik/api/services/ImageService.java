package com.demo.alkolicznik.api.services;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    @SneakyThrows
    public static void main(String[] args) {
        ImageService imageService = new ImageService();
        imageService.uploadImage();
    }

    private ImageKit imageKit;

    @SneakyThrows
    public ImageService() {
        this.imageKit = ImageKit.getInstance();
        Configuration config = readConfig();
        imageKit.setConfig(config);
        config = null;
    }

    public Result uploadImage() throws IOException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        byte[] bytes = Files.readAllBytes(Paths.get("secure" + File.separator + "sample.jpg"));
        FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, "sample_image.jpg");
        Result result = ImageKit.getInstance().upload(fileCreateRequest);
        return result;
    }

    private Configuration readConfig() {
        String endpoint = "https://ik.imagekit.io/icemajor";
        String publicKey = "public_YpQHYFb3+OX4R5aHScftYE0H0N8=";
        try {
            return new Configuration(publicKey,
                    Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
                    endpoint);
        } catch (IOException e) {
            throw new RuntimeException("Could not read secured file");
        }
    }
}
