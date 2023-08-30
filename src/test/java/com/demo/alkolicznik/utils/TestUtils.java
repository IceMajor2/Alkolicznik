package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.utils.request.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class TestUtils {

    private static ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        TestUtils.resourceLoader = resourceLoader;
    }

    public static Cookie createTokenCookie(String token) {
        return CookieUtils.createTokenCookie(token);
    }

    public static String buildURI(String uriString, Map<String, ?> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
        for (var entry : parameters.entrySet()) {
            builder
                    .queryParam(entry.getKey(), entry.getValue());
        }
        String urlTemplate = builder.encode().toUriString();
        return urlTemplate;
    }

    public static BufferedImage getBufferedImageFromWeb(String url) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage getBufferedImageFromLocal(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean imageEquals(BufferedImage imgA, BufferedImage imgB) {
        if (!dimensionsSame(imgA, imgB)) return false;
        int width = imgA.getWidth();
        int height = imgA.getHeight();
        // Loop over every pixel.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Compare the pixels for equality.
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean dimensionsSame(BufferedImage imgA, BufferedImage imgB) {
        return imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight();
    }

    public static String getRawPathToClassPathResource(String resource) {
        URI uri = null;
        try {
            uri = resourceLoader.getResource("classpath:" + resource).getURI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Paths.get(uri).toAbsolutePath().toString();
    }

    public static String getRawPathToImage(String imageFilename) {
        URI uri = null;
        try {
            uri = resourceLoader.getResource("classpath:data_img/" + imageFilename).getURI();
        } catch (IOException e) {
            try {
                uri = resourceLoader.getResource("classpath:data_img").getURI();
                return Paths.get(uri).toAbsolutePath().toString() + '\\' + imageFilename;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        String rawPath = Paths.get(uri).toAbsolutePath().toString();
        return rawPath;
    }

    public static String removeTransformationFromURL(String url) {
        StringBuilder sb = new StringBuilder("https://");
        String[] pieces = url.substring(url.indexOf("ik.imagekit")).split("/");
        for (String piece : pieces) {
            if (piece.contains("tr:")) continue;
            sb.append(piece).append('/');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
